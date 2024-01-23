import { useNavigate } from "react-router-dom";
import { Requests } from "../requests/Requests";
import { useUsosTokens } from "../contexts/UsosTokensContext";
import { useEffect, useState } from "react";
import { SecurityHelper } from "../helpers/SecurityHelper";
import { CourseParticipant } from "../model/CourseParticipant";
import { SearchResultComponent } from "./SearchResultComponent";
import "../style/teams.css";

const num_of_participant_hints = 3;

interface CreateTeamComponentProps {
    courseId: string;
    title: string;
    callback: () => void
}


interface ParticipantRow {
    index: number;
    id: string;
    value: string;
    selected: boolean;
}

export function CreateTeamComponent(props: CreateTeamComponentProps) {
    const navigate = useNavigate();
    const { token, setToken, secret, setSecret } = useUsosTokens();
    const [participants, setParticipants] = useState<CourseParticipant[]>([]);
    const [rows, setRows] = useState<ParticipantRow[]>([{ index: 0, value: "", id: "", selected: false }]);
    const [searchResults, setSearchResults] = useState<CourseParticipant[]>([]);
    const [errorMessage, setErrorMessage] = useState<string>("");

    const handleChange = (id: number, value: string) => {
        const updatedRows = rows.map(row => (row.index === id ? { ...row, value, selected: false } : row));
        setRows(updatedRows);
        searchStudent(value);
    };

    const handleAddRow = () => {
        const newRow = { index: rows.length, value: "", id: "", selected: false };
        setRows([...rows, newRow]);
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        const participantsIds = rows.filter(row => row.selected).map(row => row.id);
        if (!token || !secret) {
            return;
        }
        Requests.addTeamRequest(token, secret, props.courseId, props.title, participantsIds).then(res => res.res).then(data => {
            if (data === "SUCCESS") {
                props.callback();
                setErrorMessage("");
            } else if (data !== undefined) {
                setErrorMessage(data);
            }
        })
        .catch(error => {
            SecurityHelper.clearStorage();
            navigate("/login");
        });
    };

    const searchStudent = (value: string) => {
        let localSearchResults: CourseParticipant[] = [];
        const toSearch = value.toLowerCase();
        for (const participant of participants) {
            if (SecurityHelper.getUserId() === participant.ID) {
                continue;
            }
            if (participant.firstName.toLowerCase().includes(toSearch) ||
                    participant.lastName.toLowerCase().includes(toSearch) ||
                    (participant.firstName.toLowerCase() + " " + participant.lastName.toLowerCase()).includes(toSearch) ||
                    (participant.lastName.toLowerCase() + " " + participant.firstName.toLowerCase()).includes(toSearch)) {
                localSearchResults = [...localSearchResults, participant];
                if (localSearchResults.length >= num_of_participant_hints) {
                    break;
                }
            }
        }
        setSearchResults(localSearchResults);
    }

    const searchResultClick = (participant: CourseParticipant, rowId: number) => {
        if (rowId < 0 || rowId >= rows.length)
            return;
        const updatedRows = [...rows]
        updatedRows[rowId] = {
            ...updatedRows[rowId],
            selected: true,
            id: participant.ID,
            value: participant.firstName + " " + participant.lastName
        };
        setRows(updatedRows);
    }

    const errorMessagesMap: Map<string, string> = new Map([
        ["NONUNIQUE", "Jedna osoba nie może kilkukrotnie w danym zespole."],
        ["NO_TOPIC", "Wystąpił błąd. Ten temat nie istnieje."],
        ["TEMP_TOPIC", "Wystąpił błąd. Ten temat nie został jeszcze zaakceptowany."],
        ["SIZE_ERR", "Liczba członków musi się mieścić w granicach podanych wyżej. Pamiętaj o sobie :)"],
        ["SAME_TEAM_REQ", "Zespół o dokładnie tym samym składzie został już zgłoszony na ten temat."],
      ]);


    useEffect(() => {
        if (token && secret) {
            Requests.getCourseParticipants(token, secret, props.courseId).then(res => res.res).then(data => {
                if (data !== undefined) {
                    setParticipants(data);
                } else {
                    SecurityHelper.clearStorage();
                    navigate("/login");
                }
            })
            .catch(error => {
                SecurityHelper.clearStorage();
                navigate("/login");
            });
        }

    }, [token, setToken, secret, setSecret, props.courseId, navigate]);

    return (
        <div className="container-fluid projects-helper-create-team">
            <div>Przed wyruszeniem w drogę należy zebrać drużynę:</div>
            <form onSubmit={handleSubmit}>
                {rows.map(row => (
                    <div
                        key={row.index}
                        className={
                            `projects-helper-add-team-member-row ${row.selected ? "projects-helper-add-team-member-row-finished" : ""}`
                        }
                    >
                        <input
                            type="text"
                            value={row.value}
                            onChange={e => handleChange(row.index, e.target.value)}
                            placeholder="Wyszukaj imię lub nazwisko studenta"
                            className="form-control"
                        />
                        {row.value && !row.selected && <div className="projects-helper-floating-tip">
                            {
                                searchResults.length > 0 && searchResults.map(searchRow => (
                                    <SearchResultComponent
                                        key={searchRow.ID}
                                        participant={searchRow}
                                        rowId={row.index}
                                        onClick={searchResultClick}
                                    />
                                ))
                            }
                            {
                                searchResults.length === 0 && <div>Brak wyników</div>
                            }
                        </div>}
                    </div>
                ))}
                {
                    errorMessage !== "" &&
                    <div className="projects-helper-add-create-team-error">{errorMessagesMap.get(errorMessage)}</div>
                }
                <button type="submit" className="btn btn-primary">Stwórz zespół</button>
                <button type="button" onClick={handleAddRow} className="btn">
                    Dodaj osobę
                </button>
                <button type="button" onClick={() => props.callback()} className="btn">Anuluj</button>
            </form>
        </div>
    )
}
