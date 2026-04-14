package com.example.no1.features.service.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.no1.features.service.data.ServiceDataSource;
import com.example.no1.features.service.models.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceRepository {
    private static ServiceRepository instance;
    private ServiceDataSource dataSource;
    private MutableLiveData<List<Service>> servicesLiveData;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;

    private ServiceRepository(Context context) {
        dataSource = ServiceDataSource.getInstance(context);
        servicesLiveData = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
    }

    public static synchronized ServiceRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceRepository(context);
        }
        return instance;
    }

    public LiveData<List<Service>> getServices() {
        return servicesLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadUserServices(String userId) {
        isLoading.setValue(true);
        List<Service> services = dataSource.getServicesByUserId(userId);
        servicesLiveData.setValue(services);
        isLoading.setValue(false);
    }

    public void loadAllServices() {
        isLoading.setValue(true);
        List<Service> services = dataSource.getAllServices();
        servicesLiveData.setValue(services);
        isLoading.setValue(false);
    }

    public void submitComplaint(Service service) {
        // 验证
        if (service.getTitle() == null || service.getTitle().trim().isEmpty()) {
            errorMessage.setValue("请填写标题");
            return;
        }
        if (service.getDescription() == null || service.getDescription().trim().isEmpty()) {
            errorMessage.setValue("请填写详细描述");
            return;
        }
        if (service.getContactPhone() == null || service.getContactPhone().trim().isEmpty()) {
            errorMessage.setValue("请填写联系电话");
            return;
        }

        // 设置投诉数据
        service.setId(UUID.randomUUID().toString());
        service.setType("complaint");
        service.setStatus("pending");
        service.setCreateTime(new java.util.Date());
        service.setUpdateTime(new java.util.Date());

        List<Service.Progress> progressList = new ArrayList<>();
        progressList.add(new Service.Progress("您的投诉已提交，等待物业处理", "系统"));
        service.setProgressList(progressList);

        // 保存
        dataSource.addService(service);

        // 发送成功信号
        errorMessage.setValue("");
    }

    public void submitRepair(Service service) {
        // 验证
        if (service.getTitle() == null || service.getTitle().trim().isEmpty()) {
            errorMessage.setValue("请填写标题");
            return;
        }
        if (service.getDescription() == null || service.getDescription().trim().isEmpty()) {
            errorMessage.setValue("请填写详细描述");
            return;
        }
        if (service.getContactPhone() == null || service.getContactPhone().trim().isEmpty()) {
            errorMessage.setValue("请填写联系电话");
            return;
        }

        // 设置报修数据
        service.setId(UUID.randomUUID().toString());
        service.setType("repair");
        service.setStatus("pending");
        service.setCreateTime(new java.util.Date());
        service.setUpdateTime(new java.util.Date());

        List<Service.Progress> progressList = new ArrayList<>();
        progressList.add(new Service.Progress("您的报修已提交，等待物业接单", "系统"));
        service.setProgressList(progressList);

        // 保存
        dataSource.addService(service);

        // 发送成功信号（使用特殊标记表示成功）
        errorMessage.setValue("");
    }

    public void updateServiceStatus(String serviceId, String status, String progressContent, String operator) {
        Service service = dataSource.getServiceById(serviceId);
        if (service != null) {
            service.setStatus(status);
            service.setUpdateTime(new java.util.Date());

            List<Service.Progress> progressList = service.getProgressList();
            if (progressList == null) {
                progressList = new ArrayList<>();
            }
            progressList.add(new Service.Progress(progressContent, operator));
            service.setProgressList(progressList);

            dataSource.updateService(service);
        }
    }

    public Service getServiceById(String id) {
        return dataSource.getServiceById(id);
    }

    // 重置错误消息（在 Activity 创建时调用）
    public void resetErrorMessage() {
        errorMessage.setValue(null);
    }
}