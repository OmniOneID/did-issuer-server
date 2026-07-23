package org.omnione.did.issuer.v1.agent.service.oid4vc.status.model;

public record StatusListRotationCommand(String format, String listUri, String signingKeyId,
                                        int bits, long capacity, long ttlSeconds) {
}
