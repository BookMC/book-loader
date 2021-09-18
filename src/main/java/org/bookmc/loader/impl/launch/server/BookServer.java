package org.bookmc.loader.impl.launch.server;

import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.impl.launch.Book;

import java.lang.reflect.InvocationTargetException;

public class BookServer {
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Book.launch(args, GameEnvironment.SERVER);
    }
}
