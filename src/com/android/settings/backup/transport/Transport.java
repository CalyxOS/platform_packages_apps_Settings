package com.android.settings.backup.transport;

class Transport {
    final String name;
    final CharSequence dataManagementLabel;
    final CharSequence destinationString;

    Transport(String name, CharSequence dataManagementLabel, CharSequence destinationString) {
        this.name = name;
        this.dataManagementLabel = dataManagementLabel;
        this.destinationString = destinationString;
    }
}
