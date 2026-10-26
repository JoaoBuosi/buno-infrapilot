package dev.buno.app;
import java.time.Instant;
public record InfraRequest(String id,String environment,String service,String change,String status,String requestedBy,Instant createdAt,String decision,String auditNote){}
