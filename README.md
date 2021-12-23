# JsonLight

This is a minimalist lightweight JSON API<br>
Class and method names are freely inspired from the Google Gson API

I made this up for a strictly minimalist usage of JSON structure,
as well as to improve myself.

Also, one thing I am proud to introduce in this API is comments handling,
which isn't a thing in the Google Gson API.<br>
Comments are stored as JsonElement as well as all other elements, and can
be children of other elements (such as Arrays or Objects), but iteration
is by default set to ignore comments (for a more practical use), although
some methods allow to still iterate through them.

Please enjoy at will, and credit the author upon usage.