import texteditor.APIClasses.DataCollector

print("Now starting Emoji script....")

#Define Python class that implements one of our API callback interfaces
class EmojiHandler(texteditor.APIClasses.DataCollector):
    def collect(self, dm):
        #   Responsibility: Find IF there is a smiley face prior to the caret (dm.getcaret).
        if len(dm.getText()) >= 3 :
            #print "3 long"
            if dm.getText().find(':-)', dm.getCaretPos() - 3, dm.getCaretPos()) :
                print "ok"
                dm.text = dm.text.replace(':-)', u"\U0001f60a")
        return dm
# Make object of the EmojiHandler class
handler = EmojiHandler()

# Register it with API
api.registerTextChanged(handler)
api.addScriptName("Emoji")