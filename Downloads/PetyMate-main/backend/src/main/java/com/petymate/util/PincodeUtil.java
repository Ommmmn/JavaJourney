package com.petymate.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class PincodeUtil {

    private PincodeUtil() {}

    private static final Map<String, double[]> PINCODE_MAP = new HashMap<>();

    static {
        PINCODE_MAP.put("110001", new double[]{28.6139, 77.2090}); // New Delhi
        PINCODE_MAP.put("400001", new double[]{18.9388, 72.8354}); // Mumbai
        PINCODE_MAP.put("560001", new double[]{12.9716, 77.5946}); // Bangalore
        PINCODE_MAP.put("600001", new double[]{13.0827, 80.2707}); // Chennai
        PINCODE_MAP.put("700001", new double[]{22.5726, 88.3639}); // Kolkata
        PINCODE_MAP.put("500001", new double[]{17.3850, 78.4867}); // Hyderabad
        PINCODE_MAP.put("380001", new double[]{23.0225, 72.5714}); // Ahmedabad
        PINCODE_MAP.put("411001", new double[]{18.5204, 73.8567}); // Pune
        PINCODE_MAP.put("302001", new double[]{26.9124, 75.7873}); // Jaipur
        PINCODE_MAP.put("226001", new double[]{26.8467, 80.9462}); // Lucknow
        PINCODE_MAP.put("440001", new double[]{21.1458, 79.0882}); // Nagpur
        PINCODE_MAP.put("462001", new double[]{23.2599, 77.4126}); // Bhopal
        PINCODE_MAP.put("360001", new double[]{22.3039, 70.8022}); // Rajkot
        PINCODE_MAP.put("395001", new double[]{21.1702, 72.8311}); // Surat
        PINCODE_MAP.put("452001", new double[]{22.7196, 75.8577}); // Indore
        PINCODE_MAP.put("201001", new double[]{28.6692, 77.4538}); // Ghaziabad
        PINCODE_MAP.put("250001", new double[]{29.0588, 77.7727}); // Meerut
        PINCODE_MAP.put("144001", new double[]{31.6340, 74.8723}); // Jalandhar
        PINCODE_MAP.put("160001", new double[]{30.7333, 76.7794}); // Chandigarh
        PINCODE_MAP.put("141001", new double[]{30.9010, 75.8573}); // Ludhiana
        PINCODE_MAP.put("208001", new double[]{26.4499, 80.3319}); // Kanpur
        PINCODE_MAP.put("800001", new double[]{25.6093, 85.1376}); // Patna
        PINCODE_MAP.put("834001", new double[]{23.3441, 85.3096}); // Ranchi
        PINCODE_MAP.put("751001", new double[]{20.2961, 85.8245}); // Bhubaneswar
        PINCODE_MAP.put("781001", new double[]{26.1445, 91.7362}); // Guwahati
        PINCODE_MAP.put("682001", new double[]{9.9312, 76.2673}); // Kochi
        PINCODE_MAP.put("695001", new double[]{8.5241, 76.9366}); // Thiruvananthapuram
        PINCODE_MAP.put("570001", new double[]{12.2958, 76.6394}); // Mysore
        PINCODE_MAP.put("580001", new double[]{15.3647, 75.1240}); // Hubli
        PINCODE_MAP.put("530001", new double[]{17.6868, 83.2185}); // Visakhapatnam
        PINCODE_MAP.put("520001", new double[]{16.5062, 80.6480}); // Vijayawada
        PINCODE_MAP.put("641001", new double[]{11.0168, 76.9558}); // Coimbatore
        PINCODE_MAP.put("625001", new double[]{9.9252, 78.1198}); // Madurai
        PINCODE_MAP.put("636001", new double[]{11.6643, 78.1460}); // Salem
        PINCODE_MAP.put("600028", new double[]{12.9249, 80.1000}); // Chennai-Adyar
        PINCODE_MAP.put("400050", new double[]{19.0596, 72.8295}); // Mumbai-Bandra
        PINCODE_MAP.put("110048", new double[]{28.5494, 77.2001}); // Delhi-South
        PINCODE_MAP.put("560034", new double[]{12.9352, 77.6245}); // Bangalore-Koramangala
        PINCODE_MAP.put("500034", new double[]{17.4400, 78.3489}); // Hyderabad-Jubilee
        PINCODE_MAP.put("411014", new double[]{18.5074, 73.8077}); // Pune-Kothrud
        PINCODE_MAP.put("122001", new double[]{28.4595, 77.0266}); // Gurgaon
        PINCODE_MAP.put("201301", new double[]{28.5355, 77.3910}); // Noida
        PINCODE_MAP.put("421001", new double[]{19.2183, 72.9781}); // Thane
        PINCODE_MAP.put("380015", new double[]{23.0469, 72.5560}); // Ahmedabad-SG Highway
        PINCODE_MAP.put("452010", new double[]{22.7533, 75.8937}); // Indore-Vijay Nagar
        PINCODE_MAP.put("302020", new double[]{26.8853, 75.7607}); // Jaipur-Malviya Nagar
        PINCODE_MAP.put("226010", new double[]{26.8683, 80.9091}); // Lucknow-Gomti Nagar
        PINCODE_MAP.put("560103", new double[]{12.8340, 77.6831}); // Bangalore-Electronic City
        PINCODE_MAP.put("110092", new double[]{28.6286, 77.3202}); // Delhi-East
        PINCODE_MAP.put("400076", new double[]{19.1136, 72.8697}); // Mumbai-Powai
    }

    public static BigDecimal[] getLatLng(String pincode) {
        double[] coords = PINCODE_MAP.get(pincode);
        if (coords != null) {
            return new BigDecimal[]{BigDecimal.valueOf(coords[0]), BigDecimal.valueOf(coords[1])};
        }
        return null;
    }

    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
