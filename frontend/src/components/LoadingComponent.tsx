interface LoadingProps {
    text: string;
}

export function LoadingComponent(props: LoadingProps) {

    return (
        <div className="container-fluid projects-helper-loading">
            <div className="text-center">
                <div className="spinner-border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </div>
                <p>{props.text}...</p>
            </div>
        </div>
    )
}
