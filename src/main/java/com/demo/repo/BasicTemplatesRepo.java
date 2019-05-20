package com.demo.repo;

import com.demo.jdbc.RepositoryType;

/**
 * 
 * @author xingkai.zhang
 *
 */
public abstract class BasicTemplatesRepo extends BasicRepo {

    @Override
    protected RepositoryType repoType() {
        return RepositoryType.TEMPLATES;
    }

}
