/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.issuer.v1.agent.service.oid4vc;

import lombok.RequiredArgsConstructor;
import org.omnione.did.oid4vc.oid4vci.service.UserClaimsStore;
import org.omnione.did.oid4vc.oid4vci.service.UserDataProvider;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MockUserDataProvider implements UserDataProvider {

    private final UserClaimsStore userClaimsStore;

    @Override
    public Map<String, Object> getUserClaims(String userId, String credentialType) {
        // Try to get dynamic claims first
        System.out.println("getUserClaims - userId : " + userId + " / credentialType : " + credentialType);
        Map<String, Object> dynamicClaims = userClaimsStore.getClaims(userId, credentialType);
        if (dynamicClaims != null && !dynamicClaims.isEmpty()) {
            System.out.println("dynamicClaims load");
            
            // Create a mutable copy to modify if necessary
            Map<String, Object> claims = new HashMap<>(dynamicClaims);
            
            // Critical: portrait must be a byte[] to be encoded as a CBOR byte string (bstr)
            if (claims.containsKey("portrait") && claims.get("portrait") instanceof String) {
                try {
                    String portraitBase64 = (String) claims.get("portrait");
                    if (!"IMAGE_NOT_FOUND".equals(portraitBase64)) {
                        byte[] portraitBytes = Base64.getDecoder().decode(portraitBase64);
                        claims.put("portrait", portraitBytes);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Failed to decode portrait Base64: " + e.getMessage());
                }
            }
            return claims;
        }

        // Fallback to hardcoded values
        return switch (credentialType) {
            case "mDL", "mDoc", "mDocPID" -> Map.ofEntries(
                    Map.entry("family_name", "Kim"),
                    Map.entry("given_name", "MDOC"),
                    Map.entry("birth_date", "1990-01-01"),
                    Map.entry("issue_date", "2026-01-10T09:30:00Z"),
                    Map.entry("expiry_date", "2036-01-10T00:00:00Z"),
                    Map.entry("issuing_country", "KR"),
                    Map.entry("issuing_authority", "Korean National Police Agency"),
                    Map.entry("document_number", "11-123456-78"),
                    Map.entry("portrait", new byte[0]), // Placeholder for bstr
                    Map.entry("driving_privileges", List.of(Map.of(
                            "vehicle_category_code", "B",
                            "issue_date", "2024-01-10",
                            "expiry_date", "2034-01-10"
                    ))),
                    Map.entry("un_distinguishing_sign", "ROK")
            );
            // format : dc+sd-jwt
            case "NationalID", "NationalIDCert", "PID" -> Map.of(
                    "given_name", "Raon",
                    "family_name", "Kim",
                    "birth_date", "1990-01-01",
                    "gender", "male",
                    "nationality", "KR",
                    "id_number", "900101-1234567",
                    "address", Map.of(
                            "country", "Republic of Korea",
                            "region", "Seoul",
                            "locality", "Gangnam-gu",
                            "street_address", "Teheran-ro 123"
                    ),
                    "phone_number", "+82-10-1234-5678",
                    "email", "raonkim@raoncorp.com"
            );
            case "UCR" ->
                    Map.of("vc", "eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvbnMvY3JlZGVudGlhbHMvdjIiXSwiY3JlZGVudGlhbFNjaGVtYSI6eyJpZCI6Imh0dHA6Ly8xMC40OC4xNy4xMjQ6ODA5OS9pc3N1ZXIvYXBpL3YxL3ZjL3Zjc2NoZW1hP25hbWU9dWNyLnN0dWRlbnRfaWQiLCJ0eXBlIjoiT3NkU2NoZW1hQ3JlZGVudGlhbCJ9LCJjcmVkZW50aWFsU3ViamVjdCI6eyJjbGFpbXMiOlt7ImNhcHRpb24iOiJOYW1lIiwiY29kZSI6ImNyLmFjLnVjci5zdHVkZW50X2lkLm5hbWUiLCJmb3JtYXQiOiJwbGFpbiIsImhpZGVWYWx1ZSI6ZmFsc2UsInR5cGUiOiJ0ZXh0IiwidmFsdWUiOiJSYW9uIFBhcmsifSx7ImNhcHRpb24iOiJTdHVkZW50IElEIiwiY29kZSI6ImNyLmFjLnVjci5zdHVkZW50X2lkLnN0dWRlbnRfaWQiLCJmb3JtYXQiOiJwbGFpbiIsImhpZGVWYWx1ZSI6ZmFsc2UsInR5cGUiOiJ0ZXh0IiwidmFsdWUiOiJ1Y3IwMDEifSx7ImNhcHRpb24iOiJFbWlzaW9uIERhdGUiLCJjb2RlIjoiY3IuYWMudWNyLnN0dWRlbnRfaWQuZW1pc2lvbl9kYXRlIiwiZm9ybWF0IjoicGxhaW4iLCJoaWRlVmFsdWUiOmZhbHNlLCJ0eXBlIjoidGV4dCIsInZhbHVlIjoiMjAyNS4wOC4wMSJ9LHsiY2FwdGlvbiI6IkV4cGlyYXRpb24gRGF0ZSIsImNvZGUiOiJjci5hYy51Y3Iuc3R1ZGVudF9pZC5leHBpcmF0aW9uX2RhdGUiLCJmb3JtYXQiOiJwbGFpbiIsImhpZGVWYWx1ZSI6ZmFsc2UsInR5cGUiOiJ0ZXh0IiwidmFsdWUiOiIyMDM1LjA4LjAxIn0seyJjYXB0aW9uIjoiTWFqb3IiLCJjb2RlIjoiY3IuYWMudWNyLnN0dWRlbnRfaWQubWFqb3IiLCJmb3JtYXQiOiJwbGFpbiIsImhpZGVWYWx1ZSI6ZmFsc2UsInR5cGUiOiJ0ZXh0IiwidmFsdWUiOiLrrZTsp4DrqqjrpoQifSx7ImNhcHRpb24iOiJVbml2ZXJzaXR5IEJyYW5jaCIsImNvZGUiOiJjci5hYy51Y3Iuc3R1ZGVudF9pZC51bml2ZXJzaXR5X2JyYW5jaCIsImZvcm1hdCI6InBsYWluIiwiaGlkZVZhbHVlIjpmYWxzZSwidHlwZSI6InRleHQiLCJ2YWx1ZSI6IuydtOqyg+uPhOuqqOumhCJ9XSwiaWQiOiJkaWQ6b21uOjJtOWU0bVo2ODFOeVNEVlJMcUJOWVh0QkJDYlcifSwiZW5jb2RpbmciOiJVVEYtOCIsImV2aWRlbmNlIjpbeyJkb2N1bWVudFByZXNlbmNlIjoiUGh5c2ljYWwiLCJldmlkZW5jZURvY3VtZW50IjoiQnVzaW5lc3NMaWNlbnNlIiwic3ViamVjdFByZXNlbmNlIjoiUGh5c2ljYWwiLCJ0eXBlIjoiRG9jdW1lbnRWZXJpZmljYXRpb24iLCJ2ZXJpZmllciI6ImRpZDpvbW46dGFzIn1dLCJmb3JtYXRWZXJzaW9uIjoiMS4wIiwiaWQiOiI5NDE5NTc1ZS04ZDhjLTQ1NzctODQ5YS1lMWQwNTdjYTEzNDUiLCJpc3N1YW5jZURhdGUiOiIyMDI1LTA3LTMwVDAyOjQ1OjAzWiIsImlzc3VlciI6eyJpZCI6ImRpZDpvbW46aXNzdWVyIiwibmFtZSI6Imlzc3VlciJ9LCJsYW5ndWFnZSI6ImtvIiwicHJvb2YiOnsiY3JlYXRlZCI6IjIwMjUtMDctMzBUMDI6NDU6MDNaIiwicHJvb2ZQdXJwb3NlIjoiYXNzZXJ0aW9uTWV0aG9kIiwicHJvb2ZWYWx1ZSI6Im1JQnNDNHA4SkZjVmZVcTA3d1RnUnlYWG1tbWVBeTRLZGt3NWJmMURZTEZpcVRza1hUR3BxVjZSR1ZjQTN2Ny96NlIxUmdLUUxMeUVYdC9ZSHgwamVlbTQiLCJwcm9vZlZhbHVlTGlzdCI6WyJtSDNDdndkUGZGU3BZdzVJNjAydFhuKzFpeUU5TXBWdlVsT09iV1VpM1pCYnhIUDE0b09pWjdnOTdtOVdUNlZOVXg3ZVB5UVdlcC84TjZ2YkdaYmRNSUc0IiwibUh4VDA2eWFDVnJvQUxkWEtNWDNFMWlhT0Z3cjdPeWFqVkZTU1kwbk85SWdDS09oR2RWZFBpZjFHbS8zTWczZzBoS2NRdnVSdUhqYnUzdHptakRTaWd2byIsIm1IN0VHbVRIbnpuWTJSL2dqY0x6b21HMVIvakxJR3poSkhnL2M0QkdhQUlHTWFBYnAzekF6ZEp6L0F1TjJrSkdVRDFzL09Yd2Nad3RYUkt5elFIVXFQeU0iLCJtSUY3NjVrS1Q5WU9FMXMyVzFleFV2a09uU2dXL2EyeGhBUXc4TnI2bWRXL0xBVUp3RHpMQjZiZkFhTTRTRmJXUkY4aUkrcTRjS0J0VlhrVi8wamt0eXVNIiwibUgzQ3Z3ZFBmRlNwWXc1STYwMnRYbisxaXlFOU1wVnZVbE9PYldVaTNaQmJ4SFAxNG9PaVo3Zzk3bTlXVDZWTlV4N2VQeVFXZXAvOE42dmJHWmJkTUlHNCIsIm1IM0N2d2RQZkZTcFl3NUk2MDJ0WG4rMWl5RTlNcFZ2VWxPT2JXVWkzWkJieEhQMTRvT2laN2c5N205V1Q2Vk5VeDdlUHlRV2VwLzhONnZiR1piZE1JRzQiLCJtSVBtalp5QzFrR3hxbnZSaThqNGg2SkFrRG1XVzlUaDBUQVZUTmxVSUFCR2NBb0pSWlA0TGxFRTh5Z2x0SFVXMURBU2VxRTJMWlZpSTdpbDAwemhnbXFZIl0sInR5cGUiOiJTZWNwMjU2cjFTaWduYXR1cmUyMDE4IiwidmVyaWZpY2F0aW9uTWV0aG9kIjoiZGlkOm9tbjppc3N1ZXI/dmVyc2lvbklkPTEjYXNzZXJ0In0sInR5cGUiOlsiVmVyaWZpYWJsZUNyZWRlbnRpYWwiXSwidmFsaWRGcm9tIjoiMjAyNS0wNy0zMFQwMjo0NTowM1oiLCJ2YWxpZFVudGlsIjoiMjAyNi0wNy0zMFQwMjo0NTowM1oifQ");
            case "TEC" ->
                    Map.of("vc", "eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvbnMvY3JlZGVudGlhbHMvdjIiXSwiY3JlZGVudGlhbFNjaGVtYSI6eyJpZCI6Imh0dHA6Ly8xMC40OC4xNy4xMjQ6ODA5OS9pc3N1ZXIvYXBpL3YxL3ZjL3Zjc2NoZW1hP25hbWU9dGVjLnN0dWRlbnRfaWQiLCJ0eXBlIjoiT3NkU2NoZW1hQ3JlZGVudGlhbCJ9LCJjcmVkZW50aWFsU3ViamVjdCI6eyJjbGFpbXMiOlt7ImNhcHRpb24iOiJOYW1lIiwiY29kZSI6ImNyLmFjLnRlYy5zdHVkZW50X2lkLm5hbWUiLCJmb3JtYXQiOiJwbGFpbiIsImhpZGVWYWx1ZSI6ZmFsc2UsInR5cGUiOiJ0ZXh0IiwidmFsdWUiOiJSYW9uIEtpbSJ9LHsiY2FwdGlvbiI6IlN0dWRlbnQgSUQiLCJjb2RlIjoiY3IuYWMudGVjLnN0dWRlbnRfaWQuc3R1ZGVudF9pZCIsImZvcm1hdCI6InBsYWluIiwiaGlkZVZhbHVlIjpmYWxzZSwidHlwZSI6InRleHQiLCJ2YWx1ZSI6InRlYzAwMSJ9LHsiY2FwdGlvbiI6IkVtaXNpb24gRGF0ZSIsImNvZGUiOiJjci5hYy50ZWMuc3R1ZGVudF9pZC5lbWlzaW9uX2RhdGUiLCJmb3JtYXQiOiJwbGFpbiIsImhpZGVWYWx1ZSI6ZmFsc2UsInR5cGUiOiJ0ZXh0IiwidmFsdWUiOiIyMDI1LjA4LjAxIn0seyJjYXB0aW9uIjoiRXhwaXJhdGlvbiBEYXRlIiwiY29kZSI6ImNyLmFjLnRlYy5zdHVkZW50X2lkLmV4cGlyYXRpb25fZGF0ZSIsImZvcm1hdCI6InBsYWluIiwiaGlkZVZhbHVlIjpmYWxzZSwidHlwZSI6InRleHQiLCJ2YWx1ZSI6IjIwMzUuMDguMDEifSx7ImNhcHRpb24iOiJNYWpvciIsImNvZGUiOiJjci5hYy50ZWMuc3R1ZGVudF9pZC5tYWpvciIsImZvcm1hdCI6InBsYWluIiwiaGlkZVZhbHVlIjpmYWxzZSwidHlwZSI6InRleHQiLCJ2YWx1ZSI6IuutlOyngOuqqOumhCJ9LHsiY2FwdGlvbiI6IlVuaXZlcnNpdHkgQnJhbmNoIiwiY29kZSI6ImNyLmFjLnRlYy5zdHVkZW50X2lkLnVuaXZlcnNpdHlfYnJhbmNoIiwiZm9ybWF0IjoicGxhaW4iLCJoaWRlVmFsdWUiOmZhbHNlLCJ0eXBlIjoidGV4dCIsInZhbHVlIjoi7J206rKD64+E66qo66aEIn1dLCJpZCI6ImRpZDpvbW46Mm05ZTRtWjY4MU55U0RWUkxxQk5ZWHRCQkNiVyJ9LCJlbmNvZGluZyI6IlVURi04IiwiZXZpZGVuY2UiOlt7ImRvY3VtZW50UHJlc2VuY2UiOiJQaHlzaWNhbCIsImV2aWRlbmNlRG9jdW1lbnQiOiJCdXNpbmVzc0xpY2Vuc2UiLCJzdWJqZWN0UHJlc2VuY2UiOiJQaHlzaWNhbCIsInR5cGUiOiJEb2N1bWVudFZlcmlmaWNhdGlvbiIsInZlcmlmaWVyIjoiZGlkOm9tbjp0YXMifV0sImZvcm1hdFZlcnNpb24iOiIxLjAiLCJpZCI6Ijk0MTk1NzVlLThkOGMtNDU3Ny04NDlhLWUxZDA1N2NhMTM0NSIsImlzc3VhbmNlRGF0ZSI6IjIwMjUtMDctMzBUMDI6NDU6MDNaIiwiaXNzdWVyIjp7ImlkIjoiZGlkOm9tbjppc3N1ZXIiLCJuYW1lIjoiaXNzdWVyIn0sImxhbmd1YWdlIjoia28iLCJwcm9vZiI6eyJjcmVhdGVkIjoiMjAyNS0wNy0zMFQwMjo0NTowM1oiLCJwcm9vZlB1cnBvc2UiOiJhc3NlcnRpb25NZXRob2QiLCJwcm9vZlZhbHVlIjoibUlCc0M0cDhKRmNWZlVxMDd3VGdSeVhYbW1tZUF5NEtka3c1YmYxRFlMRmlxVHNrWFRHcHFWNlJHVmNBM3Y3L3o2UjFSZ0tRTEx5RVh0L1lIeDBqZWVtNCIsInByb29mVmFsdWVMaXN0IjpbIm1IM0N2d2RQZkZTcFl3NUk2MDJ0WG4rMWl5RTlNcFZ2VWxPT2JXVWkzWkJieEhQMTRvT2laN2c5N205V1Q2Vk5VeDdlUHlRV2VwLzhONnZiR1piZE1JRzQiLCJtSHhUMDZ5YUNWcm9BTGRYS01YM0UxaWFPRndyN095YWpWRlNTWTBuTzlJZ0NLT2hHZFZkUGlmMUdtLzNNZzNnMGhLY1F2dVJ1SGpidTN0em1qRFNpZ3ZvIiwibUg3RUdtVEhuem5ZMlIvZ2pjTHpvbUcxUi9qTElHemhKSGcvYzRCR2FBSUdNYUFicDN6QXpkSnovQXVOMmtKR1VEMXMvT1h3Y1p3dFhSS3l6UUhVcVB5TSIsIm1JRjc2NWtLVDlZT0UxczJXMWV4VXZrT25TZ1cvYTJ4aEFRdzhOcjZtZFcvTEFVSndEekxCNmJmQWFNNFNGYldSRjhpSStxNGNLQnRWWGtWLzBqa3R5dU0iLCJtSDNDdndkUGZGU3BZdzVJNjAydFhuKzFpeUU5TXBWdlVsT09iV1VpM1pCYnhIUDE0b09pWjdnOTdtOVdUNlZOVXg3ZVB5UVdlcC84TjZ2YkdaYmRNSUc0IiwibUgzQ3Z3ZFBmRlNwWXc1STYwMnRYbisxaXlFOU1wVnZVbE9PYldVaTNaQmJ4SFAxNG9PaVo3Zzk3bTlXVDZWTlV4N2VQeVFXZXAvOE42dmJHWmJkTUlHNCIsIm1JUG1qWnlDMWtHeHFudlJpOGo0aDZKQWtEbVdXOVRoMFRBVlRObFVJQUJHY0FvSlJaUDRMbEVFOHlnbHRIVVcxREFTZXFFMkxaVmlJN2lsMDB6aGdtcVkiXSwidHlwZSI6IlNlY3AyNTZyMVNpZ25hdHVyZTIwMTgiLCJ2ZXJpZmljYXRpb25NZXRob2QiOiJkaWQ6b21uOmlzc3Vlcj92ZXJzaW9uSWQ9MSNhc3NlcnQifSwidHlwZSI6WyJWZXJpZmlhYmxlQ3JlZGVudGlhbCJdLCJ2YWxpZEZyb20iOiIyMDI1LTA3LTMwVDAyOjQ1OjAzWiIsInZhbGlkVW50aWwiOiIyMDI2LTA3LTMwVDAyOjQ1OjAzWiJ9");
            default -> null;
        };
    }
}
