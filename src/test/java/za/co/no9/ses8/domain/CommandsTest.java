package za.co.no9.ses8.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.co.no9.ses8.domain.provider.EventDetail;


class CommandsTest {
    private Commands commands =
            new TestCommandsImpl();


    @Test
    void publishAnEvent() {
        EventDetail eventDetail1 =
                commands.publish(new CustomerAdded("Luke Skywalker"));

        EventDetail eventDetail2 =
                commands.publish(new CustomerAdded("Ben Kenobi"));

        Assertions.assertEquals(eventDetail1.id, 0);
        Assertions.assertEquals(eventDetail1.content, "CustomerAdded{name='Luke Skywalker'}");

        Assertions.assertEquals(eventDetail2.id, 1);
        Assertions.assertEquals(eventDetail2.content, "CustomerAdded{name='Ben Kenobi'}");
    }
}
