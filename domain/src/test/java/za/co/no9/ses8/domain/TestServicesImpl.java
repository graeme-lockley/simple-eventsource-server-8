package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.ports.Repository;

public class TestServicesImpl extends Services<TestContextImpl> {
    private TestRepositoryImpl repository =
            new TestRepositoryImpl();


    @Override
    protected Repository<TestContextImpl> repository() {
        return this.repository;
    }
}
