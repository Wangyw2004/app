package com.example.no1.features.post.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.no1.features.post.repository.PostRepository;
import java.util.List;

public class CreatePostViewModel extends AndroidViewModel {

    private PostRepository repository;
    private MutableLiveData<String> title = new MutableLiveData<>("");
    private MutableLiveData<String> content = new MutableLiveData<>("");
    private MutableLiveData<String> titleError = new MutableLiveData<>();
    private MutableLiveData<String> contentError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCreated = new MutableLiveData<>(false);

    public CreatePostViewModel(Application application) {
        super(application);
        repository = PostRepository.getInstance(application);
    }

    public LiveData<String> getTitle() { return title; }
    public LiveData<String> getContent() { return content; }
    public LiveData<String> getTitleError() { return titleError; }
    public LiveData<String> getContentError() { return contentError; }
    public LiveData<Boolean> getIsLoading() { return repository.getIsLoading(); }
    public LiveData<String> getErrorMessage() { return repository.getErrorMessage(); }
    public LiveData<Boolean> getIsCreated() { return isCreated; }

    public void setTitle(String t) {
        title.setValue(t);
        if (t != null && t.length() > 50) {
            titleError.setValue("标题不能超过50个字符");
        } else if (t != null && t.trim().isEmpty()) {
            titleError.setValue("标题不能为空");
        } else {
            titleError.setValue(null);
        }
    }

    public void setContent(String c) {
        content.setValue(c);
        if (c != null && c.trim().isEmpty()) {
            contentError.setValue("内容不能为空");
        } else {
            contentError.setValue(null);
        }
    }

    // 修改这个方法，添加 images 参数
    public void createPost(String authorId, String authorName, List<String> images) {
        String currentTitle = title.getValue();
        String currentContent = content.getValue();

        if (currentTitle == null || currentTitle.trim().isEmpty()) {
            titleError.setValue("请输入标题");
            return;
        }
        if (currentContent == null || currentContent.trim().isEmpty()) {
            contentError.setValue("请输入内容");
            return;
        }
        if (currentTitle.length() > 50) {
            titleError.setValue("标题不能超过50个字符");
            return;
        }

        repository.createPost(currentTitle, currentContent, authorId, authorName, images);
        isCreated.setValue(true);
    }
}