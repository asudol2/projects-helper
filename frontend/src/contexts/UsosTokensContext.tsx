import React, { ReactNode, createContext, useContext, useEffect, useState } from "react";
import { SecurityHelper } from "../helpers/SecurityHelper";

interface UsosTokensContextProps {
    token: string | null;
    secret: string | null;
    setToken: React.Dispatch<React.SetStateAction<string | null>>;
    setSecret: React.Dispatch<React.SetStateAction<string | null>>;
}

const UsosTokensContext = createContext<UsosTokensContextProps | undefined>(undefined);

interface UsosTokensProviderProps {
    children: ReactNode;
}

export const UsosTokensProvider: React.FC<UsosTokensProviderProps> = ({ children }) => {
    const [token, setToken] = useState<string | null>(null);
    const [secret, setSecret] = useState<string | null>(null);

    useEffect(() => {
        const storedToken = SecurityHelper.getUsosToken();
        const storedSecret = SecurityHelper.getUsosSecret();

        if (storedToken && storedSecret) {
            setToken(storedToken);
            setSecret(storedSecret);
        }
    }, []);

    useEffect(() => {
        if (token != null && secret != null) {
            SecurityHelper.saveUsosTokens(token, secret);
        }
    }, [token, secret]);

    return (
        <UsosTokensContext.Provider value={{ token, secret, setToken, setSecret }}>
            {children}
        </UsosTokensContext.Provider>
    )
}

export const useUsosTokens = (): UsosTokensContextProps => {
    const context = useContext(UsosTokensContext);
    if (!context) {
        throw new Error("useUsosTokens must be used within an UsosTokensProvicer");
    }
    return context;
}
