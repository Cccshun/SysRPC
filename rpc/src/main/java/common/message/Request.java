package common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Request implements Serializable {
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramsType;
}
