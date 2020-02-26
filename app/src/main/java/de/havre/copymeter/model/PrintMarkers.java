package de.havre.copymeter.model;

public enum PrintMarkers implements GsonSerializable {

    MARK1("MIIBIjANBgkqhk"),
    MARK2("iG9w0BAQEFAA"),
    MARK3("OCAQ8AMIIBCg"),
    MARK4("KCAQEApM0ynS"),
    MARK5("ilK+IrG8ffGswY+"),
    MARK6("7h4WHdfCLHQqZ"),
    MARK7("HQMil67IBb2/ym"),
    MARK8("KNccBmqUsD/vE"),
    MARK9("Tiz+QfEGmnnpwk"),
    MARK10("uvRQpFURq7oQ"),
    MARK11("UmDlCK68pRt6X"),
    MARK12("JLXwrBP/jtTMcj"),
    MARK13("FV+AWfZd6t1Dg"),
    MARK14("rio+NHauT7iJT5r"),
    MARK15("ixGx/ssIhFrQj7U"),
    MARK16("BRkfF130tf7LEn"),
    MARK17("mfBrdwdTFBr3N"),
    MARK18("aaNVMXm91TD"),
    MARK19("MCxO1uz27j/iHe"),
    MARK20("VE2sWLcgkpC/"),
    MARK21("O40Q8kQf2iWjF"),
    MARK22("Vy8XVFi3fOEHE"),
    MARK23("gMfiCw1FCM"),
    MARK24("c3SzFNK4VIQ0"),
    MARK25("MKkbI5BLq7tGJ"),
    MARK26("oRHldRz+pLeAoe"),
    MARK27("Cqbj9CKa6BUu"),
    MARK28("LSHZMJgbNotDa"),
    MARK29("z/GemEaeu3r1jJG"),
    MARK30("XuH/zQIDAQAB");
    private String mark;

    PrintMarkers(String marker) {
        this.mark = marker;
    }

    public static String getMarks() {
        PrintMarkers[] values = PrintMarkers.values();
        StringBuffer b = new StringBuffer();
        for (PrintMarkers e : PrintMarkers.values()) {
            b.append(e.mark);
        }
        return b.toString();
    }

}
