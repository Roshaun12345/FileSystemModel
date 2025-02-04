/**
* FileSystem
* Acts as a java file system that operates through command line interaction
*
* @author Roshaun Gregory
* @github ID Roshaun12345
* @version December 6, 2023
*/

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Used to store the data within the files and folders
class FNode {
    private String data; // Content of the file
    private boolean type; // True if it's a file, false if it's a folder
    private String name; // Name of the file or folder
    ArrayList<FNode> File; // List of files/folders within the current folder

    FNode(String i, boolean y, String n) {
        this.data = i;
        this.type = y;
        this.name = n;
        this.File = new ArrayList<>();
    }
    
    public String data() {
        return data;
    }

    public boolean type() {
        return type;
    }

    public String name() {
        return name;
    }
}

// Organizes our filesystem based on user input
class F {
    private FNode root; // Root directory
    private FNode current; // Current working directory

    F() {
        root = new FNode("/", false, "root");
        current = root;
    }

    // Add more files and folders to the system
    public void addf(String s, boolean t, String n) {
        addf(s, t, n, current);
    }

    private void addf(String info, boolean type, String title, FNode ptr) {
        if (ptr.type() == false) { // Ensure we are adding to a folder
            if (ptr.File.stream().anyMatch(s -> s.name().equals(title))) {
                System.err.println("Name already being used in this folder");
            } else {
                ptr.File.add(new FNode(info, type, title));
            }
        } else {
            System.err.println("Cannot add file to another file");
            return;
        }
    }

    // Display the contents of a file
    public void cat(String n) {
        cat(n, current);
    }

    private void cat(String title, FNode ptr) {
        int l = ptr.File.size();
        for (int i = 0; i < l; i++) {
            if (ptr.File.get(i).name().equals(title)) {
                if (ptr.File.get(i).type() == true) {
                    System.out.println(ptr.File.get(i).data());
                    return;
                } else {
                    System.err.println("Attempting to print a folder");
                    return;
                }
            }
        }
        System.err.println("File name not found in folder");
        return;
    }

    // Removes files from the system
    public void rmvfile(String n) {
        rmvfile(n, current);
    }

    private void rmvfile(String title, FNode ptr) {
        int l = ptr.File.size();
        for (int i = 0; i < l; i++) {
            if (ptr.File.get(i).name().equals(title)) {
                if (ptr.File.get(i).type() == true) {
                    ptr.File.remove(i);
                    return;
                } else {
                    System.err.println("Attempting to remove a folder");
                    return;
                }
            }
        }
        System.err.println("File name not found in folder");
        return;
    }

    // Removes folders from the system
    public void rmvfolder(String n) {
        rmvfolder(n, current);
    }

    private void rmvfolder(String title, FNode ptr) {
        int l = ptr.File.size();
        for (int i = 0; i < l; i++) {
            if (ptr.File.get(i).name().equals(title)) {
                if (ptr.File.get(i).type() == false) {
                    ptr.File.remove(i);
                    return;
                } else {
                    System.err.println("Attempting to remove a file");
                    return;
                }
            }
        }
        System.err.println("File name not found in folder");
        return;
    }

    // Used to access other directories
    public void cd1(String n) {
        current = cd1(n, current);
    }

    private FNode cd1(String title, FNode ptr) {
        if (title.equals("/")) {
            return root;
        }

        if (title.equals("..")) {
            FNode parent = ParFolder(root, ptr);
            return parent;
        }

        String a[] = title.split("/");
        int d = a.length;

        int l = ptr.File.size();
        for (int i = 0; i < l; i++) {
            if (ptr.File.get(i).name().equals(title)) {
                if (ptr.File.get(i).type() == false) {
                    return ptr.File.get(i);
                }
                if (ptr.File.get(i).type() == true) {
                    System.err.println("Attempting to change the current directory to a file");
                    return ptr;
                }
            }
        }
        return ptr;
    }

    // Lists out the contents of the current directory
    public void list() {
        list(current);
    }

    private void list(FNode ptr) {
        List<String> AlphabeticalList = new ArrayList<>();
        for (FNode child : ptr.File) {
            if (child.type() == true) {
                AlphabeticalList.add(child.name());
            }
            if (child.type() == false) {
                AlphabeticalList.add(child.name() + " (*)");
            }
        }
        AlphabeticalList.sort(null);

        for (String element : AlphabeticalList) {
            System.out.println(element);
        }
    }

    // Gives us the size of a directory in bytes
    public void du() {
        int totlasize = du(current);
        System.out.println("Size of files and sub directories in current directory is " + totlasize);
    }

    private int du(FNode ptr) {
        int total = 0;
        for (FNode child : ptr.File) {
            if (child.type() == true) {
                total = total + child.data().length();
            }
            if (child.type() == false) {
                total = total + du(child);
            }
        }
        return total;
    }

    // Prints out the path for the current working directory
    public void PrintWD() {
        String PWD = PrintWD(root, current);
        if (PWD.equals("/")) {
            System.out.println(PWD);
        } else {
            PWD = PWD.substring(5);
            System.out.println(PWD + "/" + current.name());
        }
    }

    private String PrintWD(FNode head, FNode ptr) {
        String p = "";
        if (ptr == head) {
            return "/";
        }
        for (FNode child : head.File) {
            if (child == ptr) {
                if (!head.name().equals("root")) {
                    p = "/" + head.name();
                }
                return p;
            }
            if (child.type() == false) {
                String pw = PrintWD(child, ptr);
                if (pw != null) {
                    p = "/" + head.name() + pw;
                    return p;
                }
            }
        }
        return null;
    }
}

public class FileSys {
    public static void main(String[] args) {
        F FileFolder = new F(); // Create a new file system instance
        String usercmd = "";
        Scanner scan = new Scanner(System.in);

        // Takes user input and determines which command to carry out
        do {
            System.out.print("prompt> ");
            String content = scan.nextLine();
            String[] words = content.split(" ");
            usercmd = words[0];

            if (usercmd.equals("create")) {
                if (words.length < 3) {
                    System.err.println("Insufficient input try again");
                    continue;
                }
            }
        } while (!usercmd.equals("exit")); // Loop until the user exits
    }
}
