/*
 *    Copyright 2024 Canary Prism <canaryprsn@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package canaryprism.discordbridge.api.misc;

import canaryprism.discordbridge.api.enums.DiscordBridgeEnum;
import canaryprism.discordbridge.api.enums.PartialSupport;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/// Represents a Locale supported by Discord
public enum DiscordLocale implements DiscordBridgeEnum, PartialSupport {
    
    /// The unknown locale
    UNKNOWN(""),
    
    /// `da` | Danish | Dansk
    DANISH("da"),
    
    /// `de` | German | Deutsch
    GERMAN("de"),
    
    /// `en-GB` | English, UK | English, UK
    ENGLISH_UK("en-GB"),
    
    /// `en-US` | English, US | English, US
    ENGLISH_US("en-US"),
    
    /// `es-ES` | Spanish | Español
    SPANISH("es-ES"),
    
    /// `es-419` | Spanish, LATAM | Español, LATAM
    SPANISH_LATAM("es-419"),
    
    /// `fr` | French | Français
    FRENCH("fr"),
    
    /// `hr` | Croatian | Hrvatski
    CROATIAN("hr"),
    
    /// `it` | Italian | Italiano
    ITALIAN("it"),
    
    /// `lt` | Lithuanian | Lietuviškai
    LITHUANIAN("lt"),
    
    /// `hu` | Hungarian | Magyar
    HUNGARIAN("hu"),
    
    /// `nl` | Dutch | Nederlands
    DUTCH("nl"),
    
    /// `no` | Norwegian | Norsk
    NORWEGIAN("no"),
    
    /// `pl` | Polish | Polski
    POLISH("pl"),
    
    /// `pt-BR` | Portuguese, Brazilian | Português do Brasil
    PORTUGUESE_BRAZILIAN("pt-BR"),
    
    /// `ro` | Romanian, Romania | Română
    ROMANIAN("ro"),
    
    /// `fi` | Finnish | Suomi
    FINNISH("fi"),
    
    /// `sv-SE` | Swedish | Svenska
    SWEDISH("sv-SE"),
    
    /// `vi` | Vietnamese | Tiếng Việt
    VIETNAMESE("vi"),
    
    /// `tr` | Turkish | Türkçe
    TURKISH("tr"),
    
    /// `cs` | Czech | Čeština
    CZECH("cs"),
    
    /// `el` | Greek | Ελληνικά
    GREEK("el"),
    
    /// `bg` | Bulgarian | български
    BULGARIAN("bg"),
    
    /// `ru` | Russian | Pусский
    RUSSIAN("ru"),
    
    /// `uk` | Ukrainian | Українська
    UKRAINIAN("uk"),
    
    /// `hi` | Hindi | हिन्दी
    HINDI("hi"),
    
    /// `th` | Thai | ไทย
    THAI("th"),
    
    /// `zh-CN` | Chinese, China | 中文
    CHINESE_CHINA("zh-CN"),
    
    /// `ja` | Japanese | 日本語
    JAPANESE("ja"),
    
    /// `zh-TW` | Chinese, Taiwan | 繁體中文
    CHINESE_TAIWAN("zh-TW"),
    
    /// `ko` | Korean | 한국어
    KOREAN("ko")
    ;
    
    DiscordLocale(String language_tag) {
        this.locale = Locale.forLanguageTag(language_tag);
    }
    
    /// The [Locale] representation of this DiscordLocale
    public final Locale locale;
    
    /// Gets the [Locale] representation of this DiscordLoc
    ///
    /// @return the Locale representation of this DiscordLocale
    public Locale getLocale() {
        return locale;
    }
    
    /// Gets a DiscordLocale by associated [Locale]
    ///
    /// @param locale the Java Locale to get by
    /// @return the DiscordLocale
    public static @NotNull Optional<DiscordLocale> fromLocale(@NotNull Locale locale) {
        class DiscordLocaleMapping {
            static final Map<Locale, DiscordLocale> map = Arrays.stream(DiscordLocale.values())
                    .map((e) -> Map.entry(e.locale, e))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        
        return Optional.ofNullable(DiscordLocaleMapping.map.get(locale));
    }
}
