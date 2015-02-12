package com.example.agagneja.androidchat;

/**
 * Created by agagneja on 1/28/2015.
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SecurityContextGenerator {
    private String accountNumber;

    public SecurityContextGenerator(String accountNumber) {

        this.accountNumber = accountNumber;
    }


    public String generateSecurityContext() {
        JSONObject securityContext = new JSONObject();
        JSONArray scopes = new JSONArray();
        JSONObject subject = generateSubject();
        JSONObject subjectHolder = new JSONObject();
        JSONArray subjects = new JSONArray();
        try {
            scopes.put("*");
            securityContext.put("scopes", scopes.toString());
            subjectHolder.put("subject", subject.toString());
            subjects.put(subjectHolder.toString());
            securityContext.put("subjects", subjects.toString());
            securityContext.put("actor", subject);
            securityContext.put("global_session_id", "3ilsi34jsi32300Zsdkk23sdlfjlkjsd");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return securityContext.toString();
    }

    private JSONObject generateSubject() {
        JSONObject subject = new JSONObject();
        JSONArray authClaims = new JSONArray();
        try {
            subject.put("id", "0");
            subject.put("auth_state", "LOGGEDIN");
            subject.put("account_number", accountNumber);
            authClaims.put("USERNAME");
            authClaims.put("PASSWORD");
            subject.put("auth_claims", authClaims.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return subject;
    }

}

