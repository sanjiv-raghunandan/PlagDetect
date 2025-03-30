1) Create a `config.properties` file in the same level as `Main.java`.
2) Update sql url, user and password in `config.properties` which contains the following:
    db.url=(url)
    db.user=(user)
    db.password=(password)
3) Open VS Code settings (Ctrl + ,).
   Search for "java.project.referencedLibraries".
   Click "Edit in settings.json" and add:
   "java.project.referencedLibraries": [
       "lib/*.jar"
   ]  