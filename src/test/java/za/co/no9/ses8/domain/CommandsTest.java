package za.co.no9.ses8.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class CommandsTest {
    private Commands commands =
            new TestCommandsImpl();


    @Test
    void publishAnEvent() {
        Event event1 =
                commands.publish(new CustomerAdded("Luke Skywalker"));

        Event event2 =
                commands.publish(new CustomerAdded("Ben Kenobi"));

        Assertions.assertEquals(event1.id, 0);
        Assertions.assertEquals(event1.content.toString(), "CustomerAdded{name='Luke Skywalker'}");

        Assertions.assertEquals(event2.id, 1);
        Assertions.assertEquals(event2.content.toString(), "CustomerAdded{name='Ben Kenobi'}");
    }
}
