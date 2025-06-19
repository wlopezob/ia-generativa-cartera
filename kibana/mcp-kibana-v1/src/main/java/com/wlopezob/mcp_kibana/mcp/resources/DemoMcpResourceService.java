package com.wlopezob.mcp_kibana.mcp.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.logaritex.mcp.annotation.McpResource;

import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;

@Service
public class DemoMcpResourceService {

    @McpResource(name = "ping", description = "Ping the server", uri = "ping//pong")
    public String ping() { return "pong"; }
   
    @McpResource(
        name = "user-profile",
        description = "Get the user profile",
        uri = "user-profile//{username}"
    )
    public ReadResourceResult getUserProfile(
        String username) {
        
        Map<String, String> johnProfile = new HashMap<>();
		johnProfile.put("name", username);
		johnProfile.put("email", username+"@example.com");
		johnProfile.put("age", "32");
		johnProfile.put("location", "New York");
        
        String profileInfo = formatProfileInfo(johnProfile);

        return new ReadResourceResult(List.of(new TextResourceContents(
            "user-profile//" + username,
            "text/plain",
            profileInfo
        )));
    }

    private String formatProfileInfo(Map<String, String> profile) {
		if (profile.isEmpty()) {
			return "User profile not found";
		}

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : profile.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		}
		return sb.toString().trim();
	}
}
