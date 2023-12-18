package common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcError {
    UNKNOWN_ERROR("unknown_error"),
    SERVICE_SCAN_PACKAGE_NOT_FOUND("service_scan_package_not_found"),
    CLIENT_CONNECT_SERVER_FAILURE("client_connect_server_failure"),
    SERVICE_INVOCATION_FAILURE("service_invocation_failure"),
    SERVICE_NOT_FOUND("service_no_found"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("service_not_implement_any_interface"),
    UNKNOWN_PROTOCOL("unknown_protocol"),
    UNKNOWN_SERIALIZER("unknown_serializer"),
    UNKNOWN_PACKAGE_TYPE("unknown_package_type"),
    SERIALIZER_NOT_FOUND("serializer_not_found"),
    RESPONSE_NOT_MATCH("response_not_match"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("failed_to_connect_to_service_registry"),
    REGISTER_SERVICE_FAILED("register_service_failed"),

    REQUEST_SEND_FAIL("request_send_fail");

    private final String message;
}
