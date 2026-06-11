package ra.demo.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiDataResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int status;

    public static <T> ApiDataResponse<T> success(String message, T data, int status) {
        return ApiDataResponse.<T>builder().success(true).message(message).data(data).status(status).build();
    }
}
