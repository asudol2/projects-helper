import secureLocalStorage from "react-secure-storage";


export class SecurityHelper {
    static loginTokenKey = "loginToken";

    static saveLoginToken(token: string) {
        secureLocalStorage.setItem(SecurityHelper.loginTokenKey, token);
    }

    static getLoginToken() : string | null {
        const token = secureLocalStorage.getItem(SecurityHelper.loginTokenKey);
        if (!token)
            return null;
        return token as string;
    }

    static clearStorage() {
        secureLocalStorage.clear();
    }
}