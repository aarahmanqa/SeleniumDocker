package com.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NetworkLog {
    public String apiUrl;
    public String uiUrl;
    public JsonNode postData;
    public String apiMethod;
    public String apiStatus;
    public String type;
    public String requestId;

    public String toString() {
        try {
            //return Common.objectToJSONString(this);
            return new ObjectMapper().writeValueAsString(this);
        } catch (Throwable t) {
        }
        return null;
    }
}
