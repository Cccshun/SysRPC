package common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response implements Serializable {
    private int code;
    private String message;
    private Object data;

    public static Response success(Object data_) {
        return Response.builder()
                .code(1)
                .data(data_)
                .build();
    }

    public static Response fail(String msg) {
        return Response.builder()
                .code(0)
                .message(msg)
                .build();
    }
}
