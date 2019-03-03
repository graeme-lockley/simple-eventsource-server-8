package za.co.no9.ses8.domain;

import za.co.no9.ses8.domain.provider.Repository;

public class TestCommandsImpl extends Commands {
    private TestRepositoryImpl repository =
            new TestRepositoryImpl();


    @Override
    protected Repository repository() {
        return this.repository;
    }
}
