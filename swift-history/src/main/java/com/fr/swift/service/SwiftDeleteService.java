package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.db.Where;
import com.fr.swift.exception.SwiftServiceException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.operator.delete.WhereDeleter;
import com.fr.swift.source.SourceKey;

/**
 * @author anchore
 * @date 2019/1/22
 */
@SwiftService(name = "swiftDeleteService")
@SwiftBean(name = "swiftDeleteService")
public class SwiftDeleteService extends AbstractSwiftService implements DeleteService {

    private static final long serialVersionUID = 3658186285116833374L;

    @Override
    public boolean start() throws SwiftServiceException {
        super.start();
        return true;
    }

    @Override
    public boolean shutdown() throws SwiftServiceException {
        super.shutdown();
        return true;
    }

    @Override
    public boolean delete(final SourceKey tableKey, final Where where) {
        boolean success = true;
        try {
            WhereDeleter whereDeleter = (WhereDeleter) SwiftContext.get().getBean("decrementer", tableKey);
            whereDeleter.delete(where);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            success = false;
        }
        return success;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.DELETE;
    }
}