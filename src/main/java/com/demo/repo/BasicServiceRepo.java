package com.demo.repo;

import com.demo.jdbc.RepositoryType;

/**
 * 
 * @author xingkai.zhang
 *
 */
public class BasicServiceRepo extends BasicRepo {

    @Override
    protected RepositoryType repoType() {
        return RepositoryType.SERVICE;
    }

}
