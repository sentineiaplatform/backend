package com.sentineia.complaint;

import com.sentineia.base.BaseService;

import org.springframework.stereotype.Service;

@Service
public class ComplaintCategoryService extends BaseService<ComplaintCategory> {

    public ComplaintCategoryService(ComplaintCategoryRepository repository) {
        super(repository);
    }
}
