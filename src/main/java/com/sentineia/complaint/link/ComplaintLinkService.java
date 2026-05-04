package com.sentineia.complaint.link;

import com.sentineia.complaint.complaint.Complaint;
import com.sentineia.complaint.complaint.ComplaintRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ComplaintLinkService {

    private final ComplaintLinkRepository linkRepository;
    private final ComplaintRepository complaintRepository;

    public ComplaintLinkService(ComplaintLinkRepository linkRepository, ComplaintRepository complaintRepository) {
        this.linkRepository = linkRepository;
        this.complaintRepository = complaintRepository;
    }

    /** Retorna todos os vínculos onde a denúncia aparece como source ou target. */
    public List<ComplaintLink> findAllByComplaintId(UUID complaintId) {
        List<ComplaintLink> result = new ArrayList<>();
        result.addAll(linkRepository.findBySourceIdOrderByCreatedAtDesc(complaintId));
        result.addAll(linkRepository.findByTargetIdOrderByCreatedAtDesc(complaintId));
        return result;
    }

    @Transactional
    public ComplaintLink create(UUID sourceId, UUID targetId, String linkType, String note) {
        if (sourceId.equals(targetId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uma denúncia não pode ser vinculada a si própria.");
        }
        Complaint source = requireComplaint(sourceId);
        Complaint target = requireComplaint(targetId);
        String type = linkType != null ? linkType : "RELATED";
        if (linkRepository.existsBySourceIdAndTargetIdAndLinkType(sourceId, targetId, type)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vínculo já existe.");
        }
        ComplaintLink link = new ComplaintLink();
        link.setSource(source);
        link.setTarget(target);
        link.setLinkType(type);
        link.setNote(note);
        return linkRepository.save(link);
    }

    @Transactional
    public void delete(UUID complaintId, UUID linkId) {
        linkRepository.findById(linkId).ifPresent(link -> {
            boolean owns = link.getSource().getId().equals(complaintId)
                    || link.getTarget().getId().equals(complaintId);
            if (!owns) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            linkRepository.delete(link);
        });
    }

    public UUID findByProtocol(String protocol) {
        return complaintRepository.findByProtocol(protocol)
                .map(Complaint::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Protocolo não encontrado: " + protocol));
    }

    private Complaint requireComplaint(UUID id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Denúncia não encontrada: " + id));
    }
}
