package de.havre.copymeter.persitence;

import android.content.res.AssetManager;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.havre.copymeter.common.InputStreamStringReader;
import de.havre.copymeter.common.SystemService;
import de.havre.copymeter.model.TallyConfig;
import org.apache.http.*;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.*;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.MimeTokenStream;
import roboguice.util.Ln;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Singleton
public class ImportExportService {

    @Inject
    ConfigService configService;

    @Inject
    AssetManager assetManager;

    @Inject
    TemplateService templateService;

    @Inject
    SystemService systemService;

    RequestListenerThread t;

    public String startService() throws IOException {
        int port = systemService.getFreePort();
        t = new RequestListenerThread(port);
        t.setDaemon(false);
        t.start();
        String ip = systemService.getIPAddress(true);
        return ip + ":" + port ;
    }

    public void stopService() throws IOException {
        t.stopService();
    }

    private String createFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmm");
        String timestamp = format.format(new Date());
        return "CopyMeter_" + timestamp + ".json";
    }

    static class WorkerThread extends Thread {

        private final HttpService httpservice;
        private final HttpServerConnection conn;

        public WorkerThread(final HttpService httpservice, final HttpServerConnection conn) {
            super();
            this.httpservice = httpservice;
            this.conn = conn;
        }

        public void run() {
            System.out.println("New connection thread");
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            } catch (ConnectionClosedException ex) {
                System.err.println("Client closed connection");
            } catch (IOException ex) {
                System.err.println("I/O error: " + ex.getMessage());
            } catch (HttpException ex) {
                System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
            } finally {
                try {
                    this.conn.shutdown();
                } catch (IOException ignore) {
                }
            }
        }

    }

    class HttpFileHandler implements HttpRequestHandler {


        public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {

            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
            String target = request.getRequestLine().getUri();

            if (method.equals("GET") && target.startsWith("/download")) {


                final String json = new Gson().toJson(configService.getTallyConfig());

                // Make sure to show the download dialog
                response.setHeader("Content-disposition", "attachment; filename=" + createFileName());

                EntityTemplate body = new EntityTemplate(new ContentProducer() {

                    public void writeTo(final OutputStream outstream) throws IOException {
                        OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
                        writer.write(json);
                        writer.flush();
                    }

                });
                response.setEntity(body);
            } else if (method.equals("POST") && target.startsWith("/upload")) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();


                MimeTokenStream stream = new MimeTokenStream();
                stream.parse(entity.getContent());
                try {
                    for (EntityState state = stream.getState();
                         state != EntityState.T_END_OF_STREAM;
                         state = stream.next()) {
                        switch (state) {
                            case T_BODY:
                                InputStreamStringReader i = new InputStreamStringReader(stream.getInputStream());
                                String jsonStringWithPostfix = i.readString();
                                int indexOfPostfix = jsonStringWithPostfix.indexOf("------WebKitFormBoundary");
                                String jsonString = jsonStringWithPostfix.substring(0, indexOfPostfix);
                                TallyConfig tallyConfig = new Gson().fromJson(jsonString, TallyConfig.class);
                                configService.setTallyConfig(tallyConfig);
                                break;
                        }
                    }
                    EntityTemplate body = showTemplate(TemplateService.Template.SUCCESS);
                    response.setEntity(body);
                } catch (Exception e) {
                    Ln.e(e);
                    EntityTemplate body = showTemplate(TemplateService.Template.ERROR);
                    response.setEntity(body);
                }

            } else if (method.equals("GET")) {

                EntityTemplate body = showTemplate(TemplateService.Template.SELECTOR);
                response.setEntity(body);
            } else {
                throw new MethodNotSupportedException(method + " method not supported");
            }

        }

        private EntityTemplate showTemplate(TemplateService.Template template) throws IOException {
            final String theString = templateService.readTemplate(template);

            EntityTemplate body = new EntityTemplate(new ContentProducer() {

                public void writeTo(final OutputStream outstream) throws IOException {
                    OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
                    writer.write(theString);
                    writer.flush();
                }

            });
            body.setContentType("text/html; charset=UTF-8");
            return body;
        }

    }

    class RequestListenerThread extends Thread {

        private final ServerSocket serversocket;
        private final HttpParams params;
        private final HttpService httpService;

        public RequestListenerThread(int port) throws IOException {
            this.serversocket = new ServerSocket(port);
            this.params = new BasicHttpParams();
            this.params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 1000).setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                    .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false).setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                    .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");

            // Set up the HTTP protocol processor
            HttpProcessor httpproc = new BasicHttpProcessor();

            // Set up request handlers
            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
            reqistry.register("*", new HttpFileHandler());

            // Set up the HTTP service
            this.httpService = new HttpService(httpproc, new NoConnectionReuseStrategy(), new DefaultHttpResponseFactory());
            this.httpService.setParams(this.params);
            this.httpService.setHandlerResolver(reqistry);
        }

        public void stopService() {
            try {
                serversocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            Ln.d("Listening on port " + this.serversocket.getLocalPort());
            Ln.d("Point your browser to http://localhost:8088/test/test.html");

            while (!Thread.interrupted()) {
                try {
                    // Set up HTTP connection
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    System.out.println("Incoming connection from " + socket.getInetAddress());
                    conn.bind(socket, this.params);

                    // Start worker thread
                    Thread t = new WorkerThread(this.httpService, conn);
                    t.setDaemon(true);
                    t.start();
                } catch (InterruptedIOException ex) {
                    break;
                } catch (IOException e) {
                    Ln.e("I/O error initialising connection thread: " + e.getMessage());
                    break;
                }
            }
        }
    }


}