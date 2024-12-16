package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@ExtendWith(MockitoExtension.class)
class Functional {
    private record Person(
            String title, String name, String middleName, String lastname, int age, String email) {
        public boolean isAdult() {
            return this.age > 17;
        }
    }

    @Test
    void shouldFilterDoctors() {
        List<Person> people =
                List.of(
                        new Person("Dr.", "John", "F.", "Doe", 53, "john.doe@gmail.com"),
                        new Person("Dr.", "Jean", null, "Doe", 41, "jean.doe@gmail.com"),
                        new Person("Dr.", "Charles", "Francis", "Xavier", 55, "charles@xmen.com"),
                        new Person("Mr.", "Peter", null, "Jones", 15, "p.jones@gmail.com"),
                        new Person("Ms.", "Claudia", "Anne", "Singer", 17, "cas@gmail.com"));

        List<Person> doctors = new ArrayList<>();
        for (Person p : people) {
            if (p.title.equals("Dr.")) {
                doctors.add(p);
            }
        }

        Assertions.assertEquals(3, doctors.size());
    }

    @Test
    void shouldGreetAdults() {
        List<Person> people =
                List.of(
                        new Person("Dr.", "John", "F.", "Doe", 53, "john.doe@gmail.com"),
                        new Person("Dr.", "Jean", null, "Doe", 41, null),
                        new Person("Dr.", "Charles", "Francis", "Xavier", 55, "charles@xmen.com"),
                        new Person("Mr.", "Peter", null, "Jones", 15, "p.jones@gmail.com"),
                        new Person("Ms.", "Claudia", "Anne", "Singer", 17, "cas@gmail.com"));

        List<String> greetings = new ArrayList<>();
        for (Person p : people) {
            if (p.age > 17) {
                greetings.add("Hello, " + p.title + " " + p.name + "!");
            }
        }

        Assertions.assertEquals(3, greetings.size());
        Assertions.assertTrue(greetings.contains("Hello, Dr. John!"));
        Assertions.assertTrue(greetings.contains("Hello, Dr. Jean!"));
        Assertions.assertTrue(greetings.contains("Hello, Dr. Charles!"));
    }

    @Test
    void shouldEmailAdultDoctors() {
        List<Person> people =
                List.of(
                        new Person("Dr.", "John", "F.", "Doe", 53, "john.doe@gmail.com"),
                        new Person("Dr.", "Jean", null, "Doe", 41, null),
                        new Person("Dr.", "Charles", "Francis", "Xavier", 55, "charles@xmen.com"),
                        new Person("Mr.", "Peter", null, "Jones", 15, "p.jones@gmail.com"),
                        new Person("Ms.", "Claudia", "Anne", "Singer", 17, "cas@gmail.com"));

        List<String> greetings = new ArrayList<>();
        for (Person p : people) {
            if (p.age > 17 && p.title.equals("Dr.") && p.email != null) {
                greetings.add("Emailing " + p.title + " " + p.name + " at " + p.email);
            }
        }

        Assertions.assertEquals(2, greetings.size());
        Assertions.assertTrue(greetings.contains("Emailing Dr. John at john.doe@gmail.com"));
        Assertions.assertTrue(greetings.contains("Emailing Dr. Charles at charles@xmen.com"));
    }

    @Test
    void shouldEmailAdultDoctorsFunctionally() {
        List<Person> people =
                List.of(
                        new Person("Dr.", "John", "F.", "Doe", 53, "john.doe@gmail.com"),
                        new Person("Dr.", "Jean", null, "Doe", 41, null),
                        new Person("Dr.", "Charles", "Francis", "Xavier", 55, "charles@xmen.com"),
                        new Person("Mr.", "Peter", null, "Jones", 15, "p.jones@gmail.com"),
                        new Person("Ms.", "Claudia", "Anne", "Singer", 17, "cas@gmail.com"));

        people.stream()
                .filter(
                        new Predicate<Person>() {
                            @Override
                            public boolean test(Person s) {
                                return "Dr.".equals(s.title);
                            }
                        });

        people.stream()
                .filter(
                        (person) -> {
                            return "Dr.".equals(person.title);
                        });

        Predicate<Person> isDoctor = person -> "Dr.".equals(person.title);

        List<String> functionalGreetings =
                people.stream()
                        .filter(isDoctor)
                        .filter(Person::isAdult)
                        .filter(person -> person.email != null)
                        .map(person -> "Emailing " + person.title + " " + person.name + " at " + person.email)
                        // .forEach(greeting -> functionalGreetings.add(greeting));
                        .toList();

        Optional<String> optionalEmail = Optional.ofNullable(people.get(1).email);

        optionalEmail.ifPresentOrElse(
                email -> functionalGreetings.add("Hello " + email),
                () -> System.out.println("Second person doesn't have email"));

        String email = optionalEmail.orElse("admin@gmail.com");

        Assertions.assertEquals(2, functionalGreetings.size());
        Assertions.assertTrue(functionalGreetings.contains("Emailing Dr. John at john.doe@gmail.com"));
        Assertions.assertTrue(functionalGreetings.contains("Emailing Dr. Charles at charles@xmen.com"));
    }

    private static boolean hasEmail(Person person) {
        return person.email != null;
    }
}