#CONFIG AS OF 2.4
#ALL WORDS ARE IN THEIR RESPECTIVE .YML FILES
#FORMATTING CAN BE FOUND AT dev.bukkit.org/server-mods/mutenizer/pages/main/formatting
commandsList:
# Contained in here are the commands that this plugin will censor.
- msg

Check: #This section determines what to censor.
  Commands: true
  Signs: true

BreakSignsOnSwear: true #If a player swears on a sign, this will determine if it breaks or is replaced with a message!

PunishmentType: warnings lightning damage #This will determine the action taken against a player. What is shown here is the way to activate all 3 available types!
#To turn off all punishment, change PunishmentType to warnings and change TotalWarnings to -1
MuteOrBan: ban #What is shown here is the choice of whether a player is banned or muted due to warnings loss or instaban word usage! 

Damage: 6 #This determines the amount of a damage dealt to a player upon punishment!

RemainingWarningsBeforeKick: 3 #This determines when a player is kicked for swearing!

TotalWarnings: 6 #This determines the total warnings given to a player upon login or reset.

Notify: #This section shows who gets notified! Player will 
  Player: true
  Op: true
  
Money: #This section determines if and how much a player is fined for swearing!(Requires Essentials)
  Penalty: 100
  Enabled: false
  
ResetWarningsOnBan: true #When a player is banned, this will determine if their warnings are reset.

Caps: #This determines is caps are censored as well the percentage allowed in any message!
  Percentage: 50
  Enabled: true #This will determine if they are checked!
  Enforced: true #This will determine if they result in a punishment!
  
Message: #This section controls most of the messages used, including ban reason and kick messages.
  FirstJoin: You have been given the default number of warnings!
  Swear:  
    Warning: You are not allowed to say that!
    Kick: You have been kicked for swearing!
    Ban: You have been banned for repeated swearing!
  InstaBan: You have been instantly banned for using a word on the instant ban list!
  Caps:
    Warn: You have used too many capitals!
    Kick: You were kicked for using too many capitals!
    Ban: You were banned for spamming capitals!
    
BanCommands: #If banning is enabled then this will determine the commands when a player is banned.
  Ban: ban
  InstaBan: ban 
  CapsBan: ban
  
CheckForUpdates: true #This will determine if the plugin searches for an updated version! Turn to false to increase start up time!

Compact: #This will determine how to censor messages. If Replacement = true, then profanities will be censored using the defined Char:
  Replacement: true
  Char: '*'
  
BypassCode: #This is crucial to the plugin so it doesn't punish a player twice for caps and swearing.
  Caps: 'Will be replaced'
  CapsCancel: 'Will be replaced'
  
RandomizeCodes: true #This will change the codes every time the plugin is loaded. Change to false to keep codes the same and increase start up time!