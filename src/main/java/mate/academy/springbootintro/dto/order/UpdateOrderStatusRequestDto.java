package mate.academy.springbootintro.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mate.academy.springbootintro.model.Order;

@Data
public class UpdateOrderStatusRequestDto {
    @NotNull
    private Order.Status status;
}
