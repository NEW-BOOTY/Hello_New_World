
**Rainbow Table Attack:**

A rainbow table attack uses pre-computed tables of common passwords and their corresponding hashes to crack passwords quickly. Here's a basic Java example:

```java

import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.mindrot.jbcrypt.*;


public class RainbowTableAttack {

    public static void main(String[] args) {

        // Pre-computed rainbow table

        MapString> rainbowTable = new HashMap<>();

        rainbowTable.put("password123", "hash_value");

        // Add more password-hash pairs to the rainbow table

        String passwordHash = "hash_value"; // Obtain the hashed password

        String password = rainbowTable.get(passwordHash);

        if (password != null) {

            System.out.println("Password found: " + password);

        }

    }

}

