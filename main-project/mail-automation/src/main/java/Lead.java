class Lead {
    private String name;
    private String email;

    // Getters and setters
    public String getName() {
        return name != null ? name : "";  // Return empty string if name is null
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}