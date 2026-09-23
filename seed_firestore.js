const https = require('https');

const API_KEY = "AIzaSyAh3Vn-M4WJXvpWHo-j0IkF6hPi_6Ds-eg";
const PROJECT_ID = "campusone-79e84";

function postRequest(url, data) {
    return new Promise((resolve, reject) => {
        const u = new URL(url);
        const payload = JSON.stringify(data);
        const options = {
            hostname: u.hostname,
            path: u.pathname + u.search,
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Content-Length': Buffer.byteLength(payload)
            }
        };

        const req = https.request(options, (res) => {
            let body = '';
            res.on('data', chunk => body += chunk);
            res.on('end', () => {
                try {
                    const parsed = JSON.parse(body);
                    if (res.statusCode >= 200 && res.statusCode < 300) {
                        resolve(parsed);
                    } else {
                        reject(parsed);
                    }
                } catch (e) {
                    reject(body);
                }
            });
        });
        req.on('error', reject);
        req.write(payload);
        req.end();
    });
}

function patchDocument(collection, docId, fields, idToken) {
    return new Promise((resolve, reject) => {
        const u = new URL(`https://firestore.googleapis.com/v1/projects/${PROJECT_ID}/databases/(default)/documents/${collection}/${docId}`);
        const payload = JSON.stringify({ fields });
        const options = {
            hostname: u.hostname,
            path: u.pathname + u.search,
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${idToken}`,
                'Content-Length': Buffer.byteLength(payload)
            }
        };

        const req = https.request(options, (res) => {
            let body = '';
            res.on('data', chunk => body += chunk);
            res.on('end', () => {
                if (res.statusCode >= 200 && res.statusCode < 300) {
                    resolve(JSON.parse(body));
                } else {
                    reject(body);
                }
            });
        });
        req.on('error', reject);
        req.write(payload);
        req.end();
    });
}

async function main() {
    console.log("Logging into admin account to get ID token...");
    const loginRes = await postRequest(`https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=${API_KEY}`, {
        email: "admin.demo@student.mes.ac.in",
        password: "AdminPassword123!",
        returnSecureToken: true
    });
    const idToken = loginRes.idToken;
    console.log("Got ID token successfully.");

    // 1. Seed Users
    console.log("Seeding test users...");
    await patchDocument("users", "UCsyc1iImXRcjiaYU1yc5aqurkz2", {
        uid: { stringValue: "UCsyc1iImXRcjiaYU1yc5aqurkz2" },
        name: { stringValue: "Aryan Joshi" },
        email: { stringValue: "student.demo@student.mes.ac.in" },
        role: { stringValue: "student" },
        department: { stringValue: "Computer Engineering" },
        year: { stringValue: "TY" },
        division: { stringValue: "A" },
        createdAt: { integerValue: "1727100000000" }
    }, idToken);

    await patchDocument("users", "hn25qqJC7dRnAtyq6zAKb0rWO802", {
        uid: { stringValue: "hn25qqJC7dRnAtyq6zAKb0rWO802" },
        name: { stringValue: "Prof. Rajesh Patil" },
        email: { stringValue: "admin.demo@student.mes.ac.in" },
        role: { stringValue: "admin" },
        department: { stringValue: "Administration" },
        year: { stringValue: "Staff" },
        division: { stringValue: "Main" },
        createdAt: { integerValue: "1727100000000" }
    }, idToken);

    await patchDocument("users", "qS22gWEHsncplhoccsLBQnWyjGV2", {
        uid: { stringValue: "qS22gWEHsncplhoccsLBQnWyjGV2" },
        name: { stringValue: "Neha Deshmukh" },
        email: { stringValue: "csi.lead@student.mes.ac.in" },
        role: { stringValue: "club_member" },
        clubId: { stringValue: "csi" },
        department: { stringValue: "Information Technology" },
        year: { stringValue: "Final" },
        division: { stringValue: "B" },
        createdAt: { integerValue: "1727100000000" }
    }, idToken);
    console.log("3 Test users seeded!");

    // 2. Seed 11 Clubs
    const clubs = [
        { id: "nss", name: "NSS", desc: "Community service, volunteering and social responsibility activities across college.", cat: "Social Service" },
        { id: "csi", name: "CSI", desc: "Technology, programming, computing activities and technical learning for computer engineering.", cat: "Technical" },
        { id: "gdg", name: "GDG", desc: "Developer-focused learning, technology workshops, open-source and modern developer ecosystem activities.", cat: "Technical" },
        { id: "tpc", name: "TPC", desc: "Training, placement preparation, resume building, career development and employability activities.", cat: "Career" },
        { id: "tapas", name: "TAPAS", desc: "Student personality development, aptitude training, soft skills and holistic student growth.", cat: "Development" },
        { id: "student_council", name: "Student Council", desc: "Student representation, campus governance coordination and college festival leadership.", cat: "Leadership" },
        { id: "ieee", name: "IEEE", desc: "Engineering, technology research, professional networking and technical learning.", cat: "Technical" },
        { id: "satellite_club", name: "Satellite Club", desc: "Space technology, small satellite research, aerospace and telemetry technical projects.", cat: "Aerospace" },
        { id: "spark_racing", name: "Spark Racing Team", desc: "Student formula electric vehicle development, powertrain and technical racing projects.", cat: "Automotive" },
        { id: "hyperion_racing", name: "Hyperion Racing Team", desc: "Student automotive engineering, combustion engine vehicle design and racing competitions.", cat: "Automotive" },
        { id: "vanguard_racing", name: "Vanguard Racing Team", desc: "All-terrain vehicle design, mechanical engineering, racing and technical innovation.", cat: "Automotive" }
    ];

    for (const c of clubs) {
        await patchDocument("clubs", c.id, {
            clubId: { stringValue: c.id },
            name: { stringValue: c.name },
            description: { stringValue: c.desc },
            category: { stringValue: c.cat },
            active: { booleanValue: true },
            createdAt: { integerValue: "1727100000000" }
        }, idToken);
    }
    console.log("All 11 clubs seeded!");

    // 3. Seed Announcements
    const announcements = [
        { id: "ann_1", title: "Tech Fest 2026 Registration Open", desc: "Registrations are officially open for all competitive events, project exhibitions, and coding hackathons in Tech Fest 2026.", date: "15 Sep 2026", cat: "COLLEGE ANNOUNCEMENT", clubId: "college", author: "College Administration" },
        { id: "ann_2", title: "Placement Drive Phase-1", desc: "Final year students must submit their updated verified resumes on the TPC portal by this Friday.", date: "14 Sep 2026", cat: "COLLEGE ANNOUNCEMENT", clubId: "college", author: "Training & Placement Cell" },
        { id: "ann_3", title: "Blood Donation Drive", desc: "NSS is organizing an annual campus blood donation camp in collaboration with the City Red Cross on Tuesday.", date: "12 Sep 2026", cat: "CLUB ANNOUNCEMENT", clubId: "nss", author: "NSS Lead" },
        { id: "ann_4", title: "CodeX Coding Contest", desc: "CSI presents CodeX, a 3-hour competitive programming contest covering Data Structures and Algorithms.", date: "10 Sep 2026", cat: "CLUB ANNOUNCEMENT", clubId: "csi", author: "CSI Technical Team" }
    ];
    for (const a of announcements) {
        await patchDocument("announcements", a.id, {
            id: { stringValue: a.id },
            title: { stringValue: a.title },
            description: { stringValue: a.desc },
            date: { stringValue: a.date },
            category: { stringValue: a.cat },
            clubId: { stringValue: a.clubId },
            authorName: { stringValue: a.author },
            timestamp: { integerValue: "1727100000000" },
            createdAt: { integerValue: "1727100000000" }
        }, idToken);
    }
    console.log("Announcements seeded!");

    // 4. Seed Events
    const events = [
        { id: "evt_1", title: "TECH FEST 2026", date: "15 Oct 2026", time: "10:00 AM", venue: "College Auditorium", desc: "Flagship annual national technology festival with robotics, project exhibitions, and hackathons.", org: "College Administration", clubId: "college" },
        { id: "evt_2", title: "Cultural Night 2026", date: "20 Oct 2026", time: "05:30 PM", venue: "Open Air Theater", desc: "Music, drama, and student performances celebrating campus culture.", org: "Student Council", clubId: "student_council" },
        { id: "evt_3", title: "CodeSprint Hackathon", date: "25 Oct 2026", time: "09:00 AM", venue: "Lab 301", desc: "24-hour coding sprint building web and mobile applications.", org: "CSI", clubId: "csi" }
    ];
    for (const e of events) {
        await patchDocument("events", e.id, {
            id: { stringValue: e.id },
            title: { stringValue: e.title },
            date: { stringValue: e.date },
            time: { stringValue: e.time },
            venue: { stringValue: e.venue },
            description: { stringValue: e.desc },
            organizer: { stringValue: e.org },
            clubId: { stringValue: e.clubId },
            timestamp: { integerValue: "1727100000000" },
            createdAt: { integerValue: "1727100000000" }
        }, idToken);
    }
    console.log("Events seeded!");

    // 5. Seed Resources
    const resources = [
        { id: "res_1", title: "Student Handbook 2026", desc: "Official handbook containing code of conduct, campus facilities and academic guidelines.", cat: "Student Handbook", url: "https://www.mes.ac.in", date: "10 Sep 2026" },
        { id: "res_2", title: "Bonafide Certificate Form", desc: "Application form required for educational loan and passport verification.", cat: "University Forms", url: "https://www.mes.ac.in", date: "08 Sep 2026" },
        { id: "res_3", title: "Engineering Labs Safety Manual", desc: "Mandatory safety protocols and equipment operating guidelines.", cat: "Academic Resources", url: "https://www.mes.ac.in", date: "25 Aug 2026" }
    ];
    for (const r of resources) {
        await patchDocument("resources", r.id, {
            id: { stringValue: r.id },
            title: { stringValue: r.title },
            description: { stringValue: r.desc },
            category: { stringValue: r.cat },
            url: { stringValue: r.url },
            date: { stringValue: r.date },
            timestamp: { integerValue: "1727100000000" },
            createdAt: { integerValue: "1727100000000" }
        }, idToken);
    }
    console.log("Resources seeded!");

    // 6. Seed Lost & Found
    const lostFound = [
        { id: "lf_1", title: "Casio Calculator FX-991EX", desc: "Black scientific calculator with gray cover left in Canteen Table 4.", loc: "College Canteen Table 4", date: "18 Sep 2026", status: "LOST", repName: "Aryan Joshi", contact: "student.demo@student.mes.ac.in", repBy: "UCsyc1iImXRcjiaYU1yc5aqurkz2" },
        { id: "lf_2", title: "Navy Blue Water Bottle", desc: "Milton steel water bottle found in Room 402.", loc: "Room 402 (CS Dept)", date: "19 Sep 2026", status: "FOUND", repName: "Security Office", contact: "admin.demo@student.mes.ac.in", repBy: "hn25qqJC7dRnAtyq6zAKb0rWO802" }
    ];
    for (const lf of lostFound) {
        await patchDocument("lost_found", lf.id, {
            id: { stringValue: lf.id },
            title: { stringValue: lf.title },
            description: { stringValue: lf.desc },
            location: { stringValue: lf.loc },
            date: { stringValue: lf.date },
            status: { stringValue: lf.status },
            reporterName: { stringValue: lf.repName },
            contactInfo: { stringValue: lf.contact },
            reportedBy: { stringValue: lf.repBy },
            timestamp: { integerValue: "1727100000000" },
            createdAt: { integerValue: "1727100000000" }
        }, idToken);
    }
    console.log("Lost & Found seeded!");

    // 7. Seed Notifications
    const notifications = [
        { id: "notif_1", title: "Tech Fest 2026 Registration Open", desc: "Registrations for competitive events and hackathons are now open.", date: "15 Sep 2026", cat: "COLLEGE ANNOUNCEMENT" },
        { id: "notif_2", title: "Placement Drive Phase-1", desc: "Submit your verified resume to the TPC portal by Friday.", date: "14 Sep 2026", cat: "COLLEGE ANNOUNCEMENT" },
        { id: "notif_3", title: "Blood Donation Drive", desc: "NSS Blood donation camp on Tuesday at the auditorium.", date: "12 Sep 2026", cat: "CLUB ANNOUNCEMENT" }
    ];
    for (const n of notifications) {
        await patchDocument("notifications", n.id, {
            id: { stringValue: n.id },
            title: { stringValue: n.title },
            description: { stringValue: n.desc },
            date: { stringValue: n.date },
            category: { stringValue: n.cat },
            timestamp: { integerValue: "1727100000000" },
            createdAt: { integerValue: "1727100000000" }
        }, idToken);
    }
    console.log("Notifications seeded!");

    console.log("SUCCESS: All sample data and test users successfully written to Firestore!");
}

main().catch(err => {
    console.error("Seeding error:", err);
    process.exit(1);
});
