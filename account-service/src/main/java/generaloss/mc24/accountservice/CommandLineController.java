package generaloss.mc24.accountservice;

class CommandLineController {

    private boolean closeRequest;

    public boolean isCloseRequest() {
        return closeRequest;
    }

    public void evalCommand(String[] args) {
        switch (args[0]) {
            case "help" -> this.help(args);
            case "account" -> this.account(args);
            case "quit" -> closeRequest = true;
            default -> System.out.println("Unknown command: " + args[0]);
        }
    }

    private void help(String... args) {
        System.out.println("Commands list:");
        System.out.println("  help");
        System.out.println("  account {create/remove/info} [nickname]");
        System.out.println("  quit");
    }

    private void account(String... args) {
        try{
            if(args.length < 2)
                return;
            final String option = args[1];
            switch(option){
                case "create" -> {
                    if(args.length != 4)
                        break;
                    final String nickname = args[2];
                    final String password = args[3];

                    Account.create(nickname, password);
                    System.out.println("Created account '" + nickname + "'");
                }
                case "remove" -> {
                    if(args.length != 3)
                        break;
                    final String nickname = args[2];

                    Account.delete(nickname);
                    System.out.println("Deleted account '" + nickname + "'");
                }
                case "info" -> {
                    if(args.length != 3)
                        break;
                    final String nickname = args[2];

                    final Account account = Account.load(nickname);
                    System.out.println("Account info:");
                    System.out.println("  nickname: " + nickname);
                    System.out.println("  password: " + account.getPassword());
                    System.out.println("  creation date: " + account.getCreationDate());
                }
            }
        }catch(IllegalArgumentException e) {
            System.out.println("Command execution error: " + e.getMessage());
        }
    }

}
