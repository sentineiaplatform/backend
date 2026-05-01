package com.sentineia.audit;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Redige campos sensíveis em JSON antes de gravar em auditoria.
 */
public final class AuditPayloadRedactor {

    private static final Set<String> SENSITIVE_KEYS =
            Set.of("password", "currentpassword", "newpassword", "confirmpassword", "token", "accesstoken",
                    "refreshtoken", "authorization", "secret", "clientsecret", "apikey", "apisecret");

    private AuditPayloadRedactor() {}

    public static String sanitizeJson(byte[] raw, ObjectMapper mapper, int maxChars) {
        if (raw == null || raw.length == 0) {
            return null;
        }
        try {
            JsonNode node = mapper.readTree(raw);
            redact(node);
            String s = mapper.writeValueAsString(node);
            return truncate(s, maxChars);
        } catch (Exception e) {
            return "[corpo não JSON ou ilegível — não gravado por segurança]";
        }
    }

    /** Texto simples (não JSON): só indica tamanho — evita vazar segredos em form-urlencoded. */
    public static String describeNonJsonBody(byte[] raw, int maxChars) {
        if (raw == null || raw.length == 0) {
            return null;
        }
        String preview = new String(raw, StandardCharsets.UTF_8).replace('\n', ' ').trim();
        if (preview.length() > maxChars) {
            preview = preview.substring(0, maxChars - 1) + "…";
        }
        return "[não-JSON] " + preview;
    }

    private static void redact(JsonNode node) {
        if (node == null || node.isNull()) {
            return;
        }
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> it = obj.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> e = it.next();
                String key = e.getKey();
                if (isSensitiveKey(key)) {
                    obj.set(key, TextNode.valueOf("***"));
                } else {
                    redact(e.getValue());
                }
            }
        } else if (node.isArray()) {
            ArrayNode arr = (ArrayNode) node;
            for (JsonNode item : arr) {
                redact(item);
            }
        }
    }

    private static boolean isSensitiveKey(String key) {
        if (key == null || key.isBlank()) {
            return false;
        }
        String k = key.toLowerCase(Locale.ROOT).replace("-", "").replace("_", "");
        for (String s : SENSITIVE_KEYS) {
            if (k.contains(s)) {
                return true;
            }
        }
        return false;
    }

    private static String truncate(String s, int max) {
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max - 1) + "…";
    }
}
