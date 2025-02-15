package com.fdmgroup.backend_eventhub.authenticate.ApiIndex;

import com.fdmgroup.backend_eventhub.authenticate.model.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseAccount extends ApiResponse{
    private Account account;

    public Account setAccount;
}
