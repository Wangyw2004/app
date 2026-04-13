package com.example.no1.features.auth.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.no1.data.remote.ApiService;
import com.example.no1.features.auth.models.LoginRequest;
import com.example.no1.features.auth.models.LoginResponse;

public class AuthRepository {
    private static AuthRepository instance;
    private MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private AuthRepository() {}

    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public MutableLiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void performLogin(String username, String password) {
        isLoading.setValue(true);

        LoginRequest request = new LoginRequest(username, password);

        ApiService.login(request, new ApiService.LoginCallback() {
            @Override
            public void onSuccess(LoginResponse response) {
                isLoading.setValue(false);
                loginResult.setValue(response);
                errorMessage.setValue(null);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                loginResult.setValue(null);
            }
        });
    }

    public void clearData() {
        loginResult.setValue(null);
        errorMessage.setValue(null);
        isLoading.setValue(false);
    }
}