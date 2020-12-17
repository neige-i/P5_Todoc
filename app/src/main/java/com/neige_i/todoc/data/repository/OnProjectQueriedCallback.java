package com.neige_i.todoc.data.repository;

import com.neige_i.todoc.data.model.Project;

public interface OnProjectQueriedCallback {
    void onProjectQueried(Project project);
}
