import secureLocalStorage from "react-secure-storage";


export class SecurityHelper {
    static loginTokenKey = "loginToken";
    static usosTokenKey = "usosToken";
    static usosSecretKey = "usosSecret";
    static userIdKey = "userIdKey";

    static saveLoginToken(token: string) {
        secureLocalStorage.setItem(SecurityHelper.loginTokenKey, token);
    }

    static saveUsosTokens(token: string, secret: string) {
        secureLocalStorage.setItem(SecurityHelper.usosTokenKey, token);
        secureLocalStorage.setItem(SecurityHelper.usosSecretKey, secret);
    }

    static saveUserId(id: string) {
        secureLocalStorage.setItem(SecurityHelper.userIdKey, id);
    }

    static getStringValueOrNull(key: string): string | null {
        const value = secureLocalStorage.getItem(key);
        if (!value)
            return null;
        return value as string;
    }

    static getLoginToken() : string | null {
        return SecurityHelper.getStringValueOrNull(SecurityHelper.loginTokenKey);
    }

    static getUsosToken(): string | null {
        return SecurityHelper.getStringValueOrNull(SecurityHelper.usosTokenKey);
    }

    static getUsosSecret(): string | null {
        return SecurityHelper.getStringValueOrNull(SecurityHelper.usosSecretKey);
    }

    static getUserId(): string | null {
        return SecurityHelper.getStringValueOrNull(SecurityHelper.userIdKey);
    }

    static clearStorage() {
        secureLocalStorage.clear();
    }

    static isUserLoggedIn(): boolean {
        return SecurityHelper.getUsosToken() != null && SecurityHelper.getUsosSecret() != null;
    }
}