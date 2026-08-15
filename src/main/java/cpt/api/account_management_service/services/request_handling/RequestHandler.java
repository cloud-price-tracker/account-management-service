package cpt.api.account_management_service.services.request_handling;

// Simple interface to force standardization of request handling
public interface RequestHandler<REQ, RES> {
    RES handle(REQ request);
}