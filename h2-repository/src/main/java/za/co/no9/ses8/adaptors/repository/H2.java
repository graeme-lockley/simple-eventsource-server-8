package za.co.no9.ses8.adaptors.repository;

import org.jdbi.v3.core.Jdbi;
import za.co.no9.ses8.domain.ports.Repository;

public class H2 implements Repository {
    private final Jdbi jdbi;


    public H2(Jdbi jdbi) {
        this.jdbi = jdbi;
    }


    @Override
    public za.co.no9.ses8.domain.ports.UnitOfWork newUnitOfWork() {
        return new H2UnitOfWork(jdbi);
    }
}
