package dev.buno.app;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
@RestController @RequestMapping("/api/infra-requests")
public class InfraPilotController {
 private final Map<String,InfraRequest> requests=new ConcurrentHashMap<>();
 private final Pattern safe=Pattern.compile("[a-zA-Z0-9._ -]{3,160}");
 public record Create(String environment,String service,String change,String requestedBy){}
 public record Decision(String approvedBy,String note){}
 @PostMapping public InfraRequest create(@RequestBody Create c){
  if(!Set.of("dev","staging","prod").contains(c.environment()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"environment deve ser dev, staging ou prod");
  if(c.service()==null||!safe.matcher(c.service()).matches()||c.change()==null||c.change().isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"service/change inválidos");
  String id=UUID.randomUUID().toString();
  InfraRequest r=new InfraRequest(id,c.environment(),c.service(),c.change(),"PENDING",c.requestedBy(),Instant.now(),null,null);
  requests.put(id,r); return r;
 }
 @GetMapping public Collection<InfraRequest> all(){return requests.values();}
 @PostMapping("/{id}/decision") public InfraRequest decide(@PathVariable String id,@RequestBody Decision d){
  InfraRequest old=requests.get(id); if(old==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  if(!"PENDING".equals(old.status()))throw new ResponseStatusException(HttpStatus.CONFLICT,"solicitação já decidida");
  if(d.approvedBy()==null||d.approvedBy().isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"approvedBy obrigatório");
  // Este endpoint registra aprovação; não executa terraform apply automaticamente.
  InfraRequest next=new InfraRequest(old.id(),old.environment(),old.service(),old.change(), "APPROVED",old.requestedBy(),old.createdAt(),d.approvedBy(),d.note());
  requests.put(id,next);return next;
 }
 @GetMapping("/policy") public Map<String,Object> policy(){return Map.of("principle","least privilege","productionRequiresApproval",true,"automaticApply",false,"note","A execução de Terraform deve ocorrer em runner isolado, com plano revisado e credenciais de curta duração.");}
}
