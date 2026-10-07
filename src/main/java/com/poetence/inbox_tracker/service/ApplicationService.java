package com.poetence.inbox_tracker.service;

import com.poetence.inbox_tracker.dto.ApplicationRequest;
import com.poetence.inbox_tracker.dto.ApplicationResponse;
import com.poetence.inbox_tracker.dto.EmailLogResponse;
import com.poetence.inbox_tracker.exception.NotFoundException;
import com.poetence.inbox_tracker.model.Application;
import com.poetence.inbox_tracker.model.ApplicationStatus;
import com.poetence.inbox_tracker.repository.ApplicationRepository;
import com.poetence.inbox_tracker.repository.EmailLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applications;
    private final EmailLogRepository emailLogs;

    public ApplicationService(ApplicationRepository applications, EmailLogRepository emailLogs) {
        this.applications = applications;
        this.emailLogs = emailLogs;
    }

    public ApplicationResponse create(ApplicationRequest req) {
        Application app = new Application();
        apply(app, req);
        return ApplicationResponse.from(applications.save(app));
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> list(ApplicationStatus status) {
        List<Application> found = (status == null)
                ? applications.findAll()
                : applications.findByStatus(status);
        return found.stream().map(ApplicationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ApplicationResponse get(Long id) {
        return ApplicationResponse.from(find(id));
    }

    public ApplicationResponse update(Long id, ApplicationRequest req) {
        Application app = find(id);
        apply(app, req);
        return ApplicationResponse.from(app);
    }

    public void delete(Long id) {
        applications.delete(find(id));
    }

    @Transactional(readOnly = true)
    public List<EmailLogResponse> emailsFor(Long id) {
        find(id); // throws 404 if the application doesn't exist
        return emailLogs.findByApplicationIdOrderByReceivedAtDesc(id)
                .stream().map(EmailLogResponse::from).toList();
    }

    private Application find(Long id) {
        return applications.findById(id)
                .orElseThrow(() -> new NotFoundException("Application " + id + " not found"));
    }

    private void apply(Application app, ApplicationRequest req) {
        app.setCompany(req.company());
        app.setRole(req.role());
        if (req.status() != null) {
            app.setStatus(req.status());
        }
        app.setDeadline(req.deadline());
    }
}