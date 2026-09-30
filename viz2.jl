using Base: @async, Event
using GLMakie
using Observables

const NUM_ENTITIES = 2

points = Observable([Point3f(0, 0, 0) for _ in 1:NUM_ENTITIES])
fig = Figure(resolution = (800, 600))
ax = Axis3(fig[1, 1])
sc = scatter!(ax, points)

ev = Event()

pat = r"(-?\d+\.\d+(?:E[-+]?\d+)?),(-?\d+\.\d+(?:E[-+]?\d+)?),(-?\d+\.\d+(?:E[-+]?\d+)?)"

# Create an async task to update points
task = @async begin
    entity_id = 0
    process = open(`./mvnw clean compile exec:java -Dexec.mainClass=app.Main -Dexec.args="sim"`, "r")
    for line in eachline(process)
        println(line)  # Print the line for debugging
        if occursin(pat, line)
            m = match(pat, line)
            x = parse(Float32, m.captures[1])
            y = parse(Float32, m.captures[2])
            z = parse(Float32, m.captures[3])
            points[][entity_id + 1] = Point3f(x, y, z)  # Update the observable array
            entity_id = (entity_id + 1) % NUM_ENTITIES  # Cycle through entity IDs
            # Notify the observable of changes
            autolimits!(ax)
            
        end
        sleep(0.1)  # Adjust the sleep time as needed
    end
    notify(ev)
    close(process)
end

# Display the figure
display(fig)
wait(task)
println(task.exception)
