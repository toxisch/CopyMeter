package de.havre.copymeter.client;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 27.05.14
 * Time: 19:26
 * To change this template use File | Settings | File Templates.
 */
public interface SnmpClient {

    public Map<String, Integer> getOidValues(List<String> oidList) throws SnmpClientException ;

    public Map<String, Integer> getOidModel(String baseOid) throws SnmpClientException ;

}
