import { useUsosTokens } from "../contexts/UsosTokensContext";
import { TeamRequestResponse } from "../model/TeamRequstResponse";
import { Requests } from "../requests/Requests";
import "../style/teams.css"

interface TeamComponentProps {
    key: number;
    index: number;
    teamRequest: TeamRequestResponse;
    displayCount: boolean;
    confirmed?: boolean | null;
    staffView?: boolean | null;
    onDestroy: () => void;
}

const leaveTeam = (teamRequestId: number, token: string | null, secret: string | null, 
                    callback: () => void, event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    if (token && secret) {
        if (!window.confirm("Czy na pewno chcesz opuścić zespół?")) {
            return;
        }
        Requests.rejectTeamRequest(token, secret, teamRequestId).then(res => res.res).then(data => {
            if (data) {
                callback();
            }
        })
        .catch(err => {
            console.log(err)
        });
    }
}

const confirmTeam = (teamRequestId: number, topicId: number, token: string | null, 
    secret: string | null, callback: () => void, event: React.MouseEvent<HTMLButtonElement>) => {
    event.stopPropagation();
    if (token && secret) {
        if (!window.confirm("Czy na pewno chcesz przydzielić ten zespół do tematu?")) {
            return;
        }
        Requests.confirmTeamRequest(token, secret, teamRequestId, topicId).then(res => res.res).then(data => {
            if (data) {
                callback();
            }
        })
        .catch(err => {
            console.log(err)
        });
    }
}

export function TeamComponent(props: TeamComponentProps) {
    const { token, secret } = useUsosTokens();

    return (
        <div className="projects-helper-team">
            <div>
                {
                    props.displayCount && 
                    <div className="projects-helper-team-index-label"> Zespół {props.index + 1}:</div>
                }
            </div>
            <div className="projects-helper-team-container">
                {
                    <div className="projects-helper-team-participants-container">
                        {props.teamRequest.participants.sort((a, b) => a.firstName.localeCompare(b.firstName))
                                    .map((participant, participantIndex) => (
                            <div className="projects-helper-team-participant-row" key={participantIndex}>
                                {participant.firstName} {participant.lastName}
                            </div>
                        ))}
                    </div>
                }
                { !props.staffView && !props.confirmed &&
                    <button className="btn btn-primary projects-helper-team-action-button"
                        onClick={(e) => leaveTeam(props.teamRequest.teamId, token, secret, props.onDestroy, e)}
                    >
                        Opuść zespół
                    </button>
                }
                { props.staffView && !props.confirmed &&
                    <button className="btn btn-primary projects-helper-team-action-button"
                        onClick={
                            (e) => confirmTeam(
                                props.teamRequest.teamId,
                                props.teamRequest.topicId,
                                token,
                                secret,
                                props.onDestroy,
                                e
                        )}
                    >
                        Przydziel ten temat
                    </button>
                }
            </div>
        </div>
    )
}
