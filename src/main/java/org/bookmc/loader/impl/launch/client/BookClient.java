package org.bookmc.loader.impl.launch.client;

import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.impl.launch.Book;

import java.lang.reflect.InvocationTargetException;

public class BookClient {
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Book.launch(args, GameEnvironment.CLIENT);
    }
}
