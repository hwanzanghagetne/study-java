package com.hwanzanghagetne.board.chat.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateGroupRoomRequest(
       @NotNull @Size(min = 2) List<String> memberLoginIds

) {
}
