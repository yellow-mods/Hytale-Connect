package fr.hytaleconnect.config;

public class HytaleConnectConfig {
    private String serverApiKey;
    private String apiUrl;
    private RewardConfig rewards;
    private MessagesConfig messages;
    private String voteUrl;
    private String commandName;
    private StatPushConfig stats;
    private VoteReminderConfig reminder;
    private boolean stackRewards;
    private boolean voteEnabled;

    public HytaleConnectConfig() {
        // Defaults
        this.serverApiKey = "CHANGE_ME";
        this.apiUrl = "https://hytale-top-serveur.fr/api/v1/server/CHANGE_ME";
        this.voteUrl = "https://hytale-top-serveur.fr/serveur/CHANGE_ME";
        this.commandName = "vote";
        this.rewards = new RewardConfig();
        this.messages = new MessagesConfig();
        this.stats = new StatPushConfig();
        this.reminder = new VoteReminderConfig();
        this.stackRewards = false;
        this.voteEnabled = true;
    }

    public String getServerApiKey() {
        return serverApiKey;
    }

    public String getVoteUrl() {
        return voteUrl;
    }

    public void setVoteUrl(String voteUrl) {
        this.voteUrl = voteUrl;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public RewardConfig getRewards() {
        return rewards;
    }

    public StatPushConfig getStats() {
        return stats;
    }

    public MessagesConfig getMessages() {
        return messages;
    }

    public VoteReminderConfig getReminder() {
        return reminder;
    }

    public boolean isStackRewards() {
        return stackRewards;
    }

    public void setStackRewards(boolean stackRewards) {
        this.stackRewards = stackRewards;
    }

    public boolean isVoteEnabled() {
        return voteEnabled;
    }

    public void setVoteEnabled(boolean voteEnabled) {
        this.voteEnabled = voteEnabled;
    }

    public String getVoteMessage() {
        return messages.getNotVoted();
    }

    public java.util.List<String> getRewardCommands() {
        return rewards.getCommands();
    }

    public String getRewardMessage() {
        return rewards.getMessage();
    }

    public static class RewardConfig {
        private String message;
        private java.util.List<String> commands;

        public RewardConfig() {
            this.message = "\u00A7aMerci pour votre vote ! Vous avez reçu {count} récompense(s).";
            this.commands = new java.util.ArrayList<>();
            this.commands.add("give {player} hytale:Rock_Gem_Diamond");
            this.commands.add("give {player} hytale:Ore_Gold_Sandstone");
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public java.util.List<String> getCommands() {
            return commands;
        }

        public void setCommands(java.util.List<String> commands) {
            this.commands = commands;
        }
    }

    public static class MessagesConfig {
        private String checkingVote;
        private String alreadyClaimed;
        private String notVoted;
        private String errorChecking;
        private String errorClaiming;
        private String nextVoteWait;
        private String voteBroadcast;

        public MessagesConfig() {
            this.checkingVote = "\u00A77Verification du vote en cours...";
            this.alreadyClaimed = "\u00A7eVous avez déjà récupéré votre récompense pour ce vote !";
            this.notVoted = "Vous n'avez pas encore voté ! Votez sur: {url}";
            this.errorChecking = "\u00A7cUne erreur est survenue lors de la vérification du vote.";
            this.errorClaiming = "\u00A7cUne erreur est survenue lors de la récupération de votre récompense.";
            this.nextVoteWait = "\u00A7cVous devez attendre encore {time} avant de pouvoir voter à nouveau.";
            this.voteBroadcast = "\u00A76{player} \u00A7aa voté et a récupéré ses récompenses ! /{command}";
        }

        public String getCheckingVote() {
            return checkingVote;
        }

        public void setCheckingVote(String s) {
            this.checkingVote = s;
        }

        public String getAlreadyClaimed() {
            return alreadyClaimed;
        }

        public void setAlreadyClaimed(String s) {
            this.alreadyClaimed = s;
        }

        public String getNotVoted() {
            return notVoted;
        }

        public void setNotVoted(String s) {
            this.notVoted = s;
        }

        public String getErrorChecking() {
            return errorChecking;
        }

        public void setErrorChecking(String s) {
            this.errorChecking = s;
        }

        public String getErrorClaiming() {
            return errorClaiming;
        }

        public void setErrorClaiming(String s) {
            this.errorClaiming = s;
        }

        public String getNextVoteWait() {
            return nextVoteWait;
        }

        public void setNextVoteWait(String s) {
            this.nextVoteWait = s;
        }

        public String getVoteBroadcast() {
            return voteBroadcast;
        }

        public void setVoteBroadcast(String s) {
            this.voteBroadcast = s;
        }
    }

    public static class StatPushConfig {
        private boolean enabled;
        private int intervalMinutes;
        private PrivacyConfig privacy;

        public StatPushConfig() {
            this.enabled = true;
            this.intervalMinutes = 2; // Default push every 2 minutes
            this.privacy = new PrivacyConfig();
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getIntervalMinutes() {
            return intervalMinutes;
        }

        public void setIntervalMinutes(int intervalMinutes) {
            this.intervalMinutes = intervalMinutes;
        }

        public PrivacyConfig getPrivacy() {
            return privacy;
        }

        public void setPrivacy(PrivacyConfig privacy) {
            this.privacy = privacy;
        }

        public static class PrivacyConfig {
            private boolean showPlugins = true;
            private boolean showPlayerNames = true;
            private boolean showUptime = true;
            private boolean showRAM = true;
            private boolean showWorlds = true;

            public boolean isShowPlugins() {
                return showPlugins;
            }

            public void setShowPlugins(boolean b) {
                this.showPlugins = b;
            }

            public boolean isShowPlayerNames() {
                return showPlayerNames;
            }

            public void setShowPlayerNames(boolean b) {
                this.showPlayerNames = b;
            }

            public boolean isShowUptime() {
                return showUptime;
            }

            public void setShowUptime(boolean b) {
                this.showUptime = b;
            }

            public boolean isShowRAM() {
                return showRAM;
            }

            public void setShowRAM(boolean b) {
                this.showRAM = b;
            }

            public boolean isShowWorlds() {
                return showWorlds;
            }

            public void setShowWorlds(boolean b) {
                this.showWorlds = b;
            }
        }
    }

    public static class VoteReminderConfig {
        private boolean enabled;
        private int intervalMinutes;
        private String message;

        public VoteReminderConfig() {
            this.enabled = true;
            this.intervalMinutes = 30;
            this.message = "\u00A7aN'oubliez pas de voter pour le serveur ! Tapez /{command}";
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getIntervalMinutes() {
            return intervalMinutes;
        }

        public void setIntervalMinutes(int intervalMinutes) {
            this.intervalMinutes = intervalMinutes;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
