package com.raonsecure.rslogger.model;

import com.raonsecure.rslogger.contract.RSLoggerContract;

public interface ConfigurationRepo {
    void setConfiguration(RSLoggerContract.OnConfigurationResponseCallback callback);
}
