# Rankup3

This is the branch for new locale changes to Rankup.
Locale names are in ISO-639-1 and in the folder [`src/main/resources/locale`](https://github.com/okx-code/Rankup3/tree/locale/src/main/resources/locale)

When a locale is added, it should be saved [here](https://github.com/okx-code/Rankup3/blob/locale/src/main/java/sh/okx/rankup/Rankup.java#L193), ie through adding a line such as `saveLocale("es");`

You can use the [English](https://github.com/okx-code/Rankup3/blob/locale/src/main/resources/locale/en.yml) locale to help translate.

Locales should be added or modified with a pull request, but if you wish to contribute often I can give you push access to this branch.

Here is a blank template for new locales:

```yaml
rankup:
  requirements-not-met: 
  no-rankup: 
  success-public: 
  success-private: 
  confirmation: 
  title:  
  must-prestige: 
  list:
    complete: 
    current: 
    incomplete: 
    header: ""
    footer: ""
  cooldown:
    singular: 
    plural: 
prestige:
  requirements-not-met: 
  no-prestige 

  success-public: 
  success-private: 

  confirmation: 
  title: 

  list:
    complete: 
    current: 
    incomplete: 
    header: ""
    footer: ""
  cooldown:
    singular: 
    plural: 

not-high-enough: 
not-in-ladder: 
invalid-rankup: 
```
